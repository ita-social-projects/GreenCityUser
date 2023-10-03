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
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserActivationDto;
import greencity.dto.user.UserAllFriendsDto;
import greencity.dto.user.UserAndAllFriendsWithOnlineStatusDto;
import greencity.dto.user.UserAndFriendsWithOnlineStatusDto;
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
import greencity.dto.user.UserWithOnlineStatusDto;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.filters.SearchCriteria;
import greencity.filters.UserSpecification;
import greencity.repository.LanguageRepo;
import greencity.repository.UserDeactivationRepo;
import greencity.repository.UserRepo;
import greencity.repository.options.UserFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The class provides implementation of the {@code UserService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * Autowired greencity.repository.
     */
    private final UserRepo userRepo;
    private final RestClient restClient;
    private final LanguageRepo languageRepo;
    private final UserDeactivationRepo userDeactivationRepo;
    private final GoogleApiService googleApiService;
    /**
     * Autowired mapper.
     */
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
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
    public void deleteById(Long id) {
        UserVO userVO = findById(id);
        userRepo.delete(modelMapper.map(userVO, User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO findByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.isEmpty() ? null : modelMapper.map(optionalUser.get(), UserVO.class);
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
            .collect(Collectors.toList());

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
        if (!StringUtils.isEmpty(value)) {
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
    @Override
    public UserVO updateLastVisit(UserVO userVO) {
        UserVO user = findById(userVO.getId());
        log.info(user.getLastActivityTime() + "s");
        userVO.setLastActivityTime(LocalDateTime.now());
        User updatable = modelMapper.map(userVO, User.class);
        return modelMapper.map(userRepo.save(updatable), UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    public PageableDto<UserForListDto> getUsersByFilter(FilterUserDto filterUserDto, Pageable pageable) {
        Page<User> users = userRepo.findAll(new UserFilter(filterUserDto), pageable);
        List<UserForListDto> userForListDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserForListDto.class))
                .collect(Collectors.toList());
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
     * Get profile picture path {@link String}.
     *
     * @return profile picture path {@link String}
     */
    @Override
    public String getProfilePicturePathByUserId(Long id) {
        return userRepo
            .getProfilePicturePathByUserId(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.PROFILE_PICTURE_NOT_FOUND_BY_ID + id.toString()));
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
    public UserVO updateUserProfilePicture(MultipartFile image, String email,
        String base64) {
        User user = userRepo
            .findByEmail(email)
            .orElseThrow(() -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        if (base64 != null) {
            image = modelMapper.map(base64, MultipartFile.class);
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

        GeocodingResult resultsUa = googleApiService
            .getLocationByCoordinates(userProfileDtoRequest.getLatitude(), userProfileDtoRequest.getLongitude(), 0);

        GeocodingResult resultsEn = googleApiService.getLocationByCoordinates(userProfileDtoRequest.getLatitude(),
            userProfileDtoRequest.getLongitude(), 0);

        UserLocation userLocation = new UserLocation();
        userLocation.setUsers(Collections.singletonList(user));

        initializeGeoCodingResults(initializeUkrainianGeoCodingResult(userLocation), resultsUa);
        initializeGeoCodingResults(initializeEnglishGeoCodingResult(userLocation), resultsEn);

        user.setUserLocation(new UserLocation());

        if (userProfileDtoRequest.getUserCredo() != null) {
            user.setUserCredo(userProfileDtoRequest.getUserCredo());
        }
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
                .collect(Collectors.toList()));
        }
        if (userProfileDtoRequest.getShowLocation() != null) {
            user.setShowLocation(userProfileDtoRequest.getShowLocation());
        }
        if (userProfileDtoRequest.getShowEcoPlace() != null) {
            user.setShowEcoPlace(userProfileDtoRequest.getShowEcoPlace());
        }
        if (userProfileDtoRequest.getShowShoppingList() != null) {
            user.setShowShoppingList(userProfileDtoRequest.getShowShoppingList());
        }
        userRepo.save(user);
        return UpdateConstants.getResultByLanguageCode(user.getLanguage().getCode());
    }

    private void initializeGeoCodingResults(Map<AddressComponentType, Consumer<String>> initializedMap,
        GeocodingResult geocodingResult) {
        initializedMap
            .forEach((key, value) -> Arrays.stream(geocodingResult.addressComponents)
                .forEach(addressComponent -> Arrays.stream(addressComponent.types)
                    .filter(componentType -> componentType.equals(key))
                    .forEach(componentType -> value.accept(addressComponent.longName))));
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
        return modelMapper.map(user, UserProfileDtoResponse.class);
    }

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link UserVO}'s id
     * @param userLastActivityTime - new {@link UserVO}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Override
    public void updateUserLastActivityTime(Long userId, LocalDateTime userLastActivityTime) {
        userRepo.updateUserLastActivityTime(userId, userLastActivityTime);
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
     */
    @Override
    public UserProfileStatisticsDto getUserProfileStatistics(Long userId) {
        Long amountOfPublishedNewsByUserId = restClient.findAmountOfPublishedNews(userId);
        Long amountOfAcquiredHabitsByUserId = restClient.findAmountOfAcquiredHabits(userId);
        Long amountOfHabitsInProgressByUserId = restClient.findAmountOfHabitsInProgress(userId);

        return UserProfileStatisticsDto.builder()
            .amountPublishedNews(amountOfPublishedNewsByUserId)
            .amountHabitsAcquired(amountOfAcquiredHabitsByUserId)
            .amountHabitsInProgress(amountOfHabitsInProgressByUserId)
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
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
        }
        return UserAndAllFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(new PageableDto<>(friendsWithOnlineStatusDtos, friends.getTotalElements(),
                friends.getPageable().getPageNumber(), friends.getTotalPages()))
            .build();
    }

    @Override
    public UserDeactivationReasonDto deactivateUser(Long id, List<String> userReasons) {
        User foundUser =
            userRepo.findById(id).orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        foundUser.setUserStatus(UserStatus.DEACTIVATED);
        userRepo.save(foundUser);
        String reasons = userReasons.stream().map(Object::toString).collect(Collectors.joining("/"));
        userDeactivationRepo.save(UserDeactivationReason.builder()
            .dateTimeOfDeactivation(LocalDateTime.now())
            .reason(reasons)
            .user(foundUser)
            .build());
        return UserDeactivationReasonDto.builder()
            .email(foundUser.getEmail())
            .name(foundUser.getName())
            .deactivationReasons(filterReasons(foundUser.getLanguage().getCode(), reasons))
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
                .map(filterEn -> filterEn.replace("{en}", "").trim()).collect(Collectors.toList());
        }
        if (lang.equals("ua")) {
            result = forAll.stream().filter(s -> s.contains("{ua}"))
                .map(filterEn -> filterEn.replace("{ua}", "").trim()).collect(Collectors.toList());
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
    public Optional<UserVO> findByIdAndToken(Long userId, String token) {
        User foundUser = modelMapper.map(findById(userId), User.class);

        VerifyEmail verifyEmail = foundUser.getVerifyEmail();
        if (verifyEmail != null && verifyEmail.getToken().equals(token)) {
            return Optional.of(modelMapper.map(foundUser, UserVO.class));
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<UserManagementDto> searchBy(Pageable paging, String query) {
        Page<User> page = userRepo.searchBy(paging, query);
        List<UserManagementDto> users = page.stream()
            .map(user -> modelMapper.map(user, UserManagementDto.class))
            .collect(Collectors.toList());
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
            .collect(Collectors.toList());
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
    public List<String> findAllUsersCities() {
        return userRepo.findAllUsersCities();
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
        return optionalUser.isEmpty() ? null : modelMapper.map(optionalUser.get(), UbsCustomerDto.class);
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
}
