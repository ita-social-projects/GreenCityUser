package greencity.service;

import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.constant.UpdateConstants;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.UbsCustomerDto;
import greencity.dto.achievement.UserVOAchievement;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.shoppinglist.CustomShoppingListItemResponseDto;
import greencity.dto.ubs.UbsTableCreationDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAllFriendsDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserDeactivationReasonDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserManagementUpdateDto;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserManagementViewDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserProfileDtoResponse;
import greencity.dto.user.UserProfileStatisticsDto;
import greencity.dto.user.UserRoleDto;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserUpdateDto;
import greencity.dto.user.UserVO;
import greencity.dto.user.UsersOnlineStatusRequestDto;
import greencity.dto.user.DeactivateUserRequestDto;
import greencity.dto.user.UserWithOnlineStatusDto;
import greencity.dto.user.UserLocationDto;
import greencity.entity.Language;
import greencity.entity.SocialNetwork;
import greencity.entity.SocialNetworkImage;
import greencity.entity.User;
import greencity.entity.UserDeactivationReason;
import greencity.entity.UserLocation;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.InsufficientLocationDataException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.exception.exceptions.UserDeactivationException;
import greencity.exception.exceptions.Base64DecodedException;
import greencity.filters.SearchCriteria;
import greencity.filters.UserSpecification;
import greencity.repository.LanguageRepo;
import greencity.repository.UserDeactivationRepo;
import greencity.repository.UserLocationRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * The class provides implementation of the {@code UserService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final LanguageRepo languageRepo;
    private final UserLocationRepo userLocationRepo;
    private final UserDeactivationRepo userDeactivationRepo;
    private final GoogleApiService googleApiService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;
    @Value("${greencity.time.after.last.activity}")
    private long timeAfterLastActivity;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserVO save(UserVO userVO) {
        User user = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(user), UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserRating(UserAddRatingDto userRatingDto) {
        var user = userRepo.findById(userRatingDto.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        user.setRating(user.getRating() + userRatingDto.getRating());
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findById(Long id) {
        User user = userRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        return modelMapper.map(user, UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVOAchievement findUserForAchievement(Long id) {
        User user = userRepo.findUserForAchievement(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        return modelMapper.map(user, UserVOAchievement.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserForListDto> findByPage(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .toList();
        return new PageableDto<>(
            userForListDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementDto> findUserForManagementByPage(Pageable pageable) {
        Page<User> users = userRepo.findAll(pageable);
        List<UserManagementDto> userManagementDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserManagementDto.class))
                .toList();
        return new PageableAdvancedDto<>(
            userManagementDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages(),
            users.getNumber(),
            users.hasPrevious(),
            users.hasNext(),
            users.isFirst(),
            users.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUser(Long userId, UserManagementUpdateDto dto) {
        User user = findUserById(userId);
        updateUserFromDto(dto, user);
    }

    /**
     * Method for setting data from {@link UserManagementDto} to {@link UserVO}.
     *
     * @param dto  - dto {@link UserManagementDto} with updated fields.
     * @param user {@link UserVO} to be updated.
     * @author Vasyl Zhovnir
     */
    private void updateUserFromDto(UserManagementUpdateDto dto, User user) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setUserCredo(dto.getUserCredo());
        user.setUserStatus(dto.getUserStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.map(user -> modelMapper.map(user, UserVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findAll() {
        return modelMapper.map(userRepo.findAll(), new TypeToken<List<UserVO>>() {
        }.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementVO> search(Pageable pageable,
        UserManagementViewDto userManagementViewDto) {
        Page<User> found = userRepo.findAll(buildSpecification(userManagementViewDto), pageable);
        return buildPageableAdvanceDtoFromPage(found);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UbsTableCreationDto createUbsRecord(UserVO currentUser) {
        User user = userRepo.findById(currentUser.getId()).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID));
        String uuid = user.getUuid();

        return UbsTableCreationDto.builder().uuid(uuid).build();
    }

    /**
     * {@inheritDoc}
     */
    private PageableAdvancedDto<UserManagementVO> buildPageableAdvanceDtoFromPage(Page<User> pageTags) {
        List<UserManagementVO> usersVOs = pageTags.getContent().stream()
            .map(t -> modelMapper.map(t, UserManagementVO.class))
            .toList();

        return new PageableAdvancedDto<>(
            usersVOs,
            pageTags.getTotalElements(), pageTags.getPageable().getPageNumber(),
            pageTags.getTotalPages(), pageTags.getNumber(),
            pageTags.hasPrevious(), pageTags.hasNext(),
            pageTags.isFirst(), pageTags.isLast());
    }

    /**
     * {@inheritDoc}
     */
    private UserSpecification buildSpecification(UserManagementViewDto userViewDto) {
        List<SearchCriteria> searchCriteriaList = buildSearchCriteriaList(userViewDto);

        return new UserSpecification(searchCriteriaList);
    }

    /**
     * {@inheritDoc}
     */
    private List<SearchCriteria> buildSearchCriteriaList(UserManagementViewDto userViewDto) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        setValueIfNotEmpty(searchCriteriaList, "id", userViewDto.getId());
        setValueIfNotEmpty(searchCriteriaList, "name", userViewDto.getName());
        setValueIfNotEmpty(searchCriteriaList, "email", userViewDto.getEmail());
        setValueIfNotEmpty(searchCriteriaList, "userCredo", userViewDto.getUserCredo());
        setValueIfNotEmpty(searchCriteriaList, "role", userViewDto.getRole());
        setValueIfNotEmpty(searchCriteriaList, "userStatus", userViewDto.getUserStatus());
        return searchCriteriaList;
    }

    /**
     * {@inheritDoc}
     */
    private void setValueIfNotEmpty(List<SearchCriteria> searchCriteria, String key, String value) {
        if (StringUtils.hasText(value)) {
            searchCriteria.add(SearchCriteria.builder()
                .key(key)
                .type(key)
                .value(value)
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<UserVO> findNotDeactivatedByEmail(String email) {
        log.info("email {}", email);
        User notDeactivatedByEmail = userRepo.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        log.info("user: {}", notDeactivatedByEmail);
        return Optional.of(modelMapper.map(notDeactivatedByEmail, UserVO.class));
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public Long findIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_ID_BY_EMAIL, email);
        return userRepo.findIdByEmail(email).orElseThrow(
            () -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findUuIdByEmail(String email) {
        log.info(LogMessage.IN_FIND_UUID_BY_EMAIL, email);
        return userRepo.findUuidByEmail(email).orElseThrow(
            () -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserRoleDto updateRole(Long id, Role role, String email) {
        User user = findUserById(id);
        checkIfUserCanUpdate(user, email);
        user.setRole(role);
        return modelMapper.map(user, UserRoleDto.class);
    }

    private User findUserById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID));
    }

    private void checkIfUserCanUpdate(User user, String email) {
        if (email.equals(user.getEmail())) {
            throw new BadUpdateRequestException(ErrorMessage.USER_CANT_UPDATE_THEMSELVES);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserStatusDto updateStatus(Long id, UserStatus userStatus, String email) {
        checkUpdatableUser(id, email);
        accessForUpdateUserStatus(id, email);
        UserVO userVO = findById(id);
        userVO.setUserStatus(userStatus);
        User map = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(map), UserStatusDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleDto getRoles(Long id) {
        User user = userRepo.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID));

        Role role = user.getRole();
        return RoleDto.builder()
            .roles(new Role[] {role})
            .build();
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public EmailNotification getEmailNotificationsStatuses(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return user.getEmailNotification();
    }

    /**
     * {@inheritDoc}
     */
    public PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable) {
        Page<User> users = userRepo.findAll(new UserFilter(filterUserDto), pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .toList();
        return new PageableDto<>(
            userForListDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserUpdateDto getUserUpdateDtoByEmail(String email) {
        return modelMapper.map(
            userRepo.findByEmail(email)
                .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)),
            UserUpdateDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserUpdateDto update(UserUpdateDto dto, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setName(dto.getName());
        user.setEmailNotification(dto.getEmailNotification());
        userRepo.save(user);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEmployeeEmail(String newEmployeeEmail, String uuid) {
        User user = userRepo.findUserByUuid(uuid).orElseThrow(
            () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_UUID + uuid));
        if (!user.getEmail().equals(newEmployeeEmail)) {
            if (userRepo.existsUserByEmail(newEmployeeEmail)) {
                throw new BadRequestException("This email is already in use");
            }
            user.setEmail(newEmployeeEmail);
            userRepo.save(user);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateUserRefreshToken(String refreshTokenKey, Long id) {
        return userRepo.updateUserRefreshToken(refreshTokenKey, id);
    }

    /**
     * Method which check that, if admin/moderator update role/status of himself,
     * then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void checkUpdatableUser(Long id, String email) {
        UserVO user = findByEmail(email);
        if (id.equals(user.getId())) {
            throw new BadUpdateRequestException(ErrorMessage.USER_CANT_UPDATE_THEMSELVES);
        }
    }

    /**
     * Method which check that, if moderator trying update status of admins or
     * moderators, then throw exception.
     *
     * @param id    id of updatable user.
     * @param email email of admin/moderator.
     * @author Rostyslav Khasanov
     */
    private void accessForUpdateUserStatus(Long id, String email) {
        UserVO user = findByEmail(email);
        if (user.getRole() == Role.ROLE_MODERATOR) {
            Role role = findById(id).getRole();
            if ((role == Role.ROLE_MODERATOR) || (role == Role.ROLE_ADMIN)) {
                throw new LowRoleLevelException(ErrorMessage.IMPOSSIBLE_UPDATE_USER_STATUS);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Transactional
    @Override
    public List<CustomShoppingListItemResponseDto> getAvailableCustomShoppingListItems(Long userId, Long habitId) {
        return restClient.getAllAvailableCustomShoppingListItems(userId, habitId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getActivatedUsersAmount() {
        return userRepo.countAllByUserStatus(UserStatus.ACTIVATED);
    }

    /**
     * Update user profile picture {@link UserVO}.
     *
     * @param image  {@link MultipartFile}
     * @param email  {@link String} - email of user that need to update.
     * @param base64 {@link String} - picture in base 64 format.
     * @return {@link UserVO}.
     * @author Marian Datsko
     */
    @Override
    public UserVO updateUserProfilePicture(MultipartFile image, String email, String base64) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        if (base64 != null) {
            try {
                image = modelMapper.map(base64, MultipartFile.class);
            } catch (Exception e) {
                throw new Base64DecodedException(ErrorMessage.BASE64_DECODE_MESSAGE);
            }
        }
        if (image != null) {
            String profilePicturePath;
            profilePicturePath = restClient.uploadImage(image);
            user.setProfilePicturePath(profilePicturePath);
        } else {
            throw new BadRequestException(ErrorMessage.IMAGE_EXISTS);
        }
        return modelMapper.map(userRepo.save(user), UserVO.class);
    }

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @param email {@link String} - email of user that need to update.
     */
    @Override
    public void deleteUserProfilePicture(String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        user.setProfilePicturePath(null);
        userRepo.save(user);
    }

    /**
     * Save user profile information {@link UserVO}.
     *
     * @author Marian Datsko
     */
    @Override
    public String saveUserProfile(UserProfileDtoRequest userProfileDtoRequest, String email) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        if (userProfileDtoRequest.getName() != null) {
            user.setName(userProfileDtoRequest.getName());
        }
        if (userProfileDtoRequest.getUserCredo() != null) {
            user.setUserCredo(userProfileDtoRequest.getUserCredo());
        }
        setLocationForUser(user, userProfileDtoRequest);
        List<SocialNetwork> socialNetworks = user.getSocialNetworks();
        if (userProfileDtoRequest.getSocialNetworks() != null) {
            socialNetworks.forEach(socialNetwork -> restClient.deleteSocialNetwork(socialNetwork.getId()));
            user.getSocialNetworks().clear();
            user.getSocialNetworks().addAll(userProfileDtoRequest.getSocialNetworks()
                .stream()
                .map(url -> SocialNetwork.builder()
                    .url(url)
                    .user(user)
                    .socialNetworkImage(modelMapper.map(restClient.getSocialNetworkImageByUrl(url),
                        SocialNetworkImage.class))
                    .build())
                .toList());
        }
        user.setShowLocation(userProfileDtoRequest.getShowLocation());
        user.setShowEcoPlace(userProfileDtoRequest.getShowEcoPlace());
        user.setShowShoppingList(userProfileDtoRequest.getShowShoppingList());

        userRepo.save(user);
        return UpdateConstants.getResultByLanguageCode(user.getLanguage().getCode());
    }

    private void setLocationForUser(User user, UserProfileDtoRequest userProfileDtoRequest) {
        if (shouldSkipLocationUpdate(user, userProfileDtoRequest)) {
            return;
        }

        if (user.getUserLocation() != null && (userProfileDtoRequest.getCoordinates().getLatitude() == null
            || userProfileDtoRequest.getCoordinates().getLongitude() == null)) {
            UserLocation old = user.getUserLocation();
            old.getUsers().remove(user);
            user.setUserLocation(null);
        } else {
            GeocodingResult resultsUk = googleApiService.getLocationByCoordinates(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude(),
                "uk");
            GeocodingResult resultsEn = googleApiService.getLocationByCoordinates(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude(),
                "en");
            UserLocation userLocation = userLocationRepo.getUserLocationByLatitudeAndLongitude(
                userProfileDtoRequest.getCoordinates().getLatitude(),
                userProfileDtoRequest.getCoordinates().getLongitude()).orElse(new UserLocation());

            /*
             * check if user already has a location and if he is the only one assigned to
             * this location. If user do not have a location check if such location is in
             * database, if true then assign it to user, if not - add new location to
             * database and assign it to user. If user has a location and this location
             * belongs only to him, modify this location. If user has a location but there
             * are more users assigned to this location, then create a new location for this
             * user. If user inserted same location get his location and do not change
             * anything.
             */
            if (user.getUserLocation() != null && user.getUserLocation().getUsers().size() == 1) {
                if (userLocation.getId() != null && user.getUserLocation() != userLocation) {
                    UserLocation deleteLocation = user.getUserLocation();
                    user.setUserLocation(userLocation);
                    userLocationRepo.delete(deleteLocation);
                } else {
                    userLocation = user.getUserLocation();
                }
            } else if (user.getUserLocation() != null && user.getUserLocation().getUsers().size() > 1) {
                UserLocation old = user.getUserLocation();
                old.getUsers().remove(user);
            }
            initializeGeoCodingResults(initializeUkrainianGeoCodingResult(userLocation), resultsUk);
            initializeGeoCodingResults(initializeEnglishGeoCodingResult(userLocation), resultsEn);
            userLocation.setLatitude(userProfileDtoRequest.getCoordinates().getLatitude());
            userLocation.setLongitude(userProfileDtoRequest.getCoordinates().getLongitude());
            userLocation = userLocationRepo.save(userLocation);
            user.setUserLocation(userLocation);
        }
    }

    private boolean shouldSkipLocationUpdate(User user, UserProfileDtoRequest userProfileDtoRequest) {
        return user.getUserLocation() == null
            && (userProfileDtoRequest.getCoordinates().getLatitude() == null
                || userProfileDtoRequest.getCoordinates().getLongitude() == null);
    }

    private void initializeGeoCodingResults(Map<AddressComponentType, Consumer<String>> initializedMap,
        GeocodingResult geocodingResult) {
        checkGeocodingResultContainsAllInformation(geocodingResult, initializedMap.size());
        initializedMap
            .forEach((key, value) -> Arrays.stream(geocodingResult.addressComponents)
                .forEach(addressComponent -> Arrays.stream(addressComponent.types)
                    .filter(componentType -> componentType.equals(key))
                    .forEach(componentType -> value.accept(addressComponent.longName))));
    }

    private void checkGeocodingResultContainsAllInformation(GeocodingResult geocodingResult, int size) {
        if (geocodingResult.addressComponents.length <= size) {
            throw new InsufficientLocationDataException(ErrorMessage.INSUFFICIENT_LOCATION_DATA_FOUND);
        }
    }

    private Map<AddressComponentType, Consumer<String>> initializeEnglishGeoCodingResult(
        UserLocation userLocation) {
        return Map.of(
            AddressComponentType.LOCALITY, userLocation::setCityEn,
            AddressComponentType.COUNTRY, userLocation::setCountryEn,
            AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1, userLocation::setRegionEn);
    }

    private Map<AddressComponentType, Consumer<String>> initializeUkrainianGeoCodingResult(
        UserLocation userLocation) {
        return Map.of(
            AddressComponentType.LOCALITY, userLocation::setCityUa,
            AddressComponentType.COUNTRY, userLocation::setCountryUa,
            AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1, userLocation::setRegionUa);
    }

    /**
     * Method return user profile information {@link UserVO}.
     *
     * @author Marian Datsko
     */
    @Override
    public UserProfileDtoResponse getUserProfileInformation(Long userId) {
        User user = userRepo
            .findById(userId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        UserProfileDtoResponse userProfileDtoResponse = new UserProfileDtoResponse();
        if (user.getUserLocation() != null) {
            userProfileDtoResponse.setUserLocationDto(modelMapper.map(user.getUserLocation(), UserLocationDto.class));
        }
        modelMapper.map(user, userProfileDtoResponse);
        return userProfileDtoResponse;
    }

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @param userId {@link Long}
     * @return {@link Boolean}.
     * @author Yurii Zhurakovskyi
     */
    @Override
    public boolean checkIfTheUserIsOnline(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        Optional<Timestamp> lastActivityTime = userRepo.findLastActivityTimeById(userId);
        if (lastActivityTime.isPresent()) {
            LocalDateTime userLastActivityTime = lastActivityTime.get().toLocalDateTime();
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime lastActivityTimeZDT = ZonedDateTime.of(userLastActivityTime, ZoneId.systemDefault());
            long result = now.toInstant().toEpochMilli() - lastActivityTimeZDT.toInstant().toEpochMilli();
            return result <= timeAfterLastActivity;
        }
        return false;
    }

    /**
     * Method return user profile statistics {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     * @author Marian Datsko
     * @author Olena Sotnik
     */
    @Override
    public UserProfileStatisticsDto getUserProfileStatistics(Long userId) {
        Long amountOfPublishedNewsByUserId = restClient.findAmountOfPublishedNews(userId);
        Long amountOfAcquiredHabitsByUserId = restClient.findAmountOfAcquiredHabits(userId);
        Long amountOfHabitsInProgressByUserId = restClient.findAmountOfHabitsInProgress(userId);
        Long amountOfOrganizedAndAttendedEventsByUserId = restClient
            .findAmountOfEventsOrganizedAndAttendedByUser(userId);

        return UserProfileStatisticsDto.builder()
            .amountPublishedNews(amountOfPublishedNewsByUserId)
            .amountHabitsAcquired(amountOfAcquiredHabitsByUserId)
            .amountHabitsInProgress(amountOfHabitsInProgressByUserId)
            .amountOrganizedAndAttendedEvents(amountOfOrganizedAndAttendedEventsByUserId)
            .build();
    }

    /**
     * Get user and six friends with the online status {@link UserVO}.
     *
     * @param userId {@link Long}
     * @author Yurii Zhurakovskyi
     */
    @Override
    public UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(Long userId) {
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(checkIfTheUserIsOnline(userId))
            .build();
        List<User> sixFriendsWithTheHighestRating = userRepo.getSixFriendsWithTheHighestRating(userId);
        List<UserWithOnlineStatusDto> sixFriendsWithOnlineStatusDtos = new ArrayList<>();
        if (!sixFriendsWithTheHighestRating.isEmpty()) {
            sixFriendsWithOnlineStatusDtos = sixFriendsWithTheHighestRating
                .stream()
                .map(u -> new UserWithOnlineStatusDto(u.getId(), checkIfTheUserIsOnline(u.getId())))
                .toList();
        }
        return UserAndFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(sixFriendsWithOnlineStatusDtos)
            .build();
    }

    /**
     * Get user and all friends with the online status {@link UserVO} by page.
     *
     * @param userId   {@link Long}
     * @param pageable {@link Pageable }
     * @author Yurii Zhurakovskyi
     */
    @Override
    public UserAndAllFriendsWithOnlineStatusDto getAllFriendsWithTheOnlineStatus(Long userId, Pageable pageable) {
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(checkIfTheUserIsOnline(userId))
            .build();
        Page<User> friends = userRepo.getAllUserFriends(userId, pageable);
        List<UserWithOnlineStatusDto> friendsWithOnlineStatusDtos = new ArrayList<>();
        if (!friends.isEmpty()) {
            friendsWithOnlineStatusDtos = friends
                .getContent()
                .stream()
                .map(u -> new UserWithOnlineStatusDto(u.getId(), checkIfTheUserIsOnline(u.getId())))
                .toList();
        }
        return UserAndAllFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(new PageableDto<>(friendsWithOnlineStatusDtos, friends.getTotalElements(),
                friends.getPageable().getPageNumber(), friends.getTotalPages()))
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDeactivationReasonDto deactivateUser(String uuid, DeactivateUserRequestDto request, UserVO userVO) {
        User requestedUser = userRepo.findById(userVO.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userVO.getId()));

        User foundUser = userRepo.findUserByUuid(uuid)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_UUID + uuid));

        if (requestedUser.getId().equals(foundUser.getId())) {
            if (requestedUser.getRole().equals(Role.ROLE_USER)) {
                return deactivateAndLogReason(foundUser, request.getReason());
            } else {
                throw new UserDeactivationException(ErrorMessage.USER_CANNOT_DEACTIVATE_YOURSELF);
            }
        }

        if (foundUser.getRole().equals(Role.ROLE_USER)) {
            if (isAuthorizedToDeactivate(requestedUser.getRole())) {
                return deactivateAndLogReason(foundUser, request.getReason());
            } else {
                throw new UserDeactivationException(ErrorMessage.USER_CANNOT_DEACTIVATE_OTHERS);
            }
        }

        if (requestedUser.getRole().equals(Role.ROLE_ADMIN)) {
            if (!foundUser.getRole().equals(Role.ROLE_ADMIN)) {
                return deactivateAndLogReason(foundUser, request.getReason());
            } else {
                throw new UserDeactivationException(ErrorMessage.ADMIN_CANNOT_DEACTIVATE_OTHER_ADMIN);
            }
        }

        throw new UserDeactivationException(ErrorMessage.YOU_DO_NOT_HAVE_PERMISSIONS_TO_DEACTIVATE_THIS_USER);
    }

    /**
     * Helper method to determine if a user is authorized to deactivate other users.
     *
     * @param role the role of the requesting user
     * @return true if the user is authorized to deactivate others, false otherwise
     */
    private boolean isAuthorizedToDeactivate(Role role) {
        return role.equals(Role.ROLE_ADMIN)
            || role.equals(Role.ROLE_MODERATOR)
            || role.equals(Role.ROLE_EMPLOYEE)
            || role.equals(Role.ROLE_UBS_EMPLOYEE);
    }

    /**
     * Performs user deactivation and logs the deactivation reason.
     *
     * @param foundUser the user to deactivate
     * @param reason    the reason for deactivation
     * @return a UserDeactivationReasonDto object containing deactivation details
     */
    private UserDeactivationReasonDto deactivateAndLogReason(User foundUser, String reason) {
        foundUser.setUserStatus(UserStatus.DEACTIVATED);
        userRepo.save(foundUser);
        userDeactivationRepo.save(UserDeactivationReason.builder()
            .dateTimeOfDeactivation(LocalDateTime.now())
            .reason(reason)
            .user(foundUser)
            .build());
        return UserDeactivationReasonDto.builder()
            .email(foundUser.getEmail())
            .name(foundUser.getName())
            .deactivationReasons(List.of(reason))
            .lang(foundUser.getLanguage().getCode())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDeactivationReason(Long id, String adminLang) {
        UserDeactivationReason userReason = userDeactivationRepo.getLastDeactivationReasons(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_DEACTIVATION_REASON_IS_EMPTY));
        if (adminLang.equals("uk")) {
            adminLang = "ua";
        }
        return filterReasons(adminLang,
            userReason.getReason());
    }

    private List<String> filterReasons(String lang, String reasons) {
        List<String> result = null;
        List<String> forAll = List.of(reasons.split("/"));
        if (lang.equals("en")) {
            result = forAll.stream().filter(s -> s.contains("{en}"))
                .map(filterEn -> filterEn.replace("{en}", "").trim()).toList();
        }
        if (lang.equals("ua")) {
            result = forAll.stream().filter(s -> s.contains("{ua}"))
                .map(filterEn -> filterEn.replace("{ua}", "").trim()).toList();
        }
        return result;
    }

    @Transactional
    @Override
    public UserActivationDto setActivatedStatus(Long id) {
        User foundUser =
            userRepo.findById(id).orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        foundUser.setUserStatus(UserStatus.ACTIVATED);
        userRepo.save(foundUser);
        return UserActivationDto.builder()
            .email(foundUser.getEmail())
            .name(foundUser.getName())
            .lang(foundUser.getLanguage().getCode())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserLanguage(Long userId, Long languageId) {
        Language language = languageRepo.findById(languageId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.LANGUAGE_NOT_FOUND_BY_ID + languageId));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        user.setLanguage(language);
        userRepo.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<Long> deactivateAllUsers(List<Long> listId) {
        userRepo.deactivateSelectedUsers(listId);
        return listId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementDto> searchBy(Pageable paging, String query) {
        Page<User> page = userRepo.searchBy(paging, query);
        List<UserManagementDto> users = page.stream()
            .map(user -> modelMapper.map(user, UserManagementDto.class))
            .toList();
        return new PageableAdvancedDto<>(
            users,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages(),
            page.getNumber(),
            page.hasPrevious(),
            page.hasNext(),
            page.isFirst(),
            page.isLast());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserVO> findAllByEmailNotification(EmailNotification emailNotification) {
        return userRepo.findAllByEmailNotification(emailNotification).stream()
            .map(user -> modelMapper.map(user, UserVO.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int scheduleDeleteDeactivatedUsers() {
        return userRepo.scheduleDeleteDeactivatedUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserCityDto findAllUsersCities(Long userId) {
        UserLocation userLocation = userLocationRepo.findAllUsersCities(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_DID_NOT_SET_ANY_CITY));
        return modelMapper.map(userLocation, UserCityDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserAllFriendsDto> findUserByName(String name, Pageable page, Long id) {
        Page<User> ourUsersList = userRepo.findAllUsersByName(name, page, id);
        return getUserAllFriendsDtoPageableDto(id, ourUsersList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Long> findAllRegistrationMonthsMap() {
        return userRepo.findAllRegistrationMonthsMap();
    }

    private List<UserAllFriendsDto> allUsersMutualFriendsRecommendedOrRequest(Long id,
        List<UserAllFriendsDto> recommendedFriends) {
        List<User> allUserFriends = userRepo.getAllUserFriends(id);
        for (UserAllFriendsDto currentFriend : recommendedFriends) {
            long mutualFriendsCount = 0;
            List<User> allCurrentUserFriends = userRepo.getAllUserFriends(currentFriend.getId());
            for (User friendUser : allCurrentUserFriends) {
                for (User user : allUserFriends) {
                    if (friendUser.getId().equals(user.getId())) {
                        mutualFriendsCount++;
                    }
                }
            }
            currentFriend.setMutualFriends(mutualFriendsCount);
        }
        return recommendedFriends;
    }

    @Override
    public UbsCustomerDto findByUUid(String uuid) {
        Optional<User> optionalUser = userRepo.findUserByUuid(uuid);
        return optionalUser.map(user -> modelMapper.map(user, UbsCustomerDto.class)).orElse(null);
    }

    @Override
    public void markUserAsDeactivated(String uuid) {
        User user = userRepo.findUserByUuid(uuid).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_UUID));
        user.setUserStatus(UserStatus.DEACTIVATED);
        userRepo.save(user);
    }

    @Override
    public void markUserAsActivated(String uuid) {
        User user = userRepo.findUserByUuid(uuid).orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_UUID));
        user.setUserStatus(UserStatus.ACTIVATED);
        userRepo.save(user);
    }

    @Override
    public UserVO findAdminById(Long id) {
        User user = userRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID));

        boolean isAdmin = user.getRole().equals(Role.ROLE_ADMIN);

        if (isAdmin) {
            return modelMapper.map(user, UserVO.class);
        }

        throw new LowRoleLevelException("You do not have authorities");
    }

    private PageableDto<UserAllFriendsDto> getUserAllFriendsDtoPageableDto(Long userId, Page<User> allUsers) {
        List<UserAllFriendsDto> allFriends = modelMapper
            .map(allUsers.getContent(),
                new TypeToken<List<UserAllFriendsDto>>() {
                }.getType());
        allFriends.forEach(f -> f.setFriendsChatDto(restClient.chatBetweenTwo(f.getId(), userId)));
        return new PageableDto<>(
            allUsersMutualFriendsRecommendedOrRequest(userId, allFriends),
            allUsers.getTotalElements(),
            allUsers.getPageable().getPageNumber(),
            allUsers.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean checkIfUserExistsByUuid(String uuid) {
        return userRepo.findUserByUuid(uuid).isPresent();
    }

    @Override
    public void updateUserLastActivityTimeByEmail(String email, LocalDateTime userLastActivityTime) {
        userRepo.updateUserLastActivityTimeByEmail(email, userLastActivityTime);
    }

    @Override
    @Transactional
    public void checkUsersOnlineStatus(UsersOnlineStatusRequestDto request) {
        List<User> users = userRepo.getAllUsersByUsersId(request.getUsersId());

        List<UserWithOnlineStatusDto> usersWithOnlineStatus = users.stream()
            .map(user -> UserWithOnlineStatusDto.builder()
                .id(user.getId())
                .onlineStatus(checkIfTheUserIsOnline(user.getId()))
                .build())
            .toList();

        messagingTemplate.convertAndSend("/topic/" + request.getCurrentUserId() + "/usersOnlineStatus",
            usersWithOnlineStatus);
    }
}
