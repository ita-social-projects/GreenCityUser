package greencity.dto.user;

public record GreenCityUserProfileDtoResponse(
    Long userId,
    String profilePicturePath,
    String userCredo,
    Double userRating,
    UserLocationDto userLocationDto) {
}
