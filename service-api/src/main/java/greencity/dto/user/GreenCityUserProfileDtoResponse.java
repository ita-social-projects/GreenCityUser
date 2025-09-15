package greencity.dto.user;

public record GreenCityUserProfileDtoResponse(
    Long userId,
    String profilePicturePath,
    Double userRating,
    UserLocationDto userLocationDto) {
}
