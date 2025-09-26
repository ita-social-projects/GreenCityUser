package greencity.dto.user;

public record GreenCityUserProfileDtoResponse(
    Long userId,
    String email,
    String profilePicturePath,
    Double userRating,
    UserLocationDto userLocationDto) {
}
