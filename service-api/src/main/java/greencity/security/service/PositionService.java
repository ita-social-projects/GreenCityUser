package greencity.security.service;

import greencity.dto.position.PositionAuthoritiesDto;
import java.util.List;

public interface PositionService {
    /**
     * Method that gets an employee`s positions and all possible related authorities
     * to these positions.
     *
     * @param email {@link String} - employee email.
     * @return {@link PositionAuthoritiesDto}.
     *
     * @author Anton Bondar
     */
    PositionAuthoritiesDto getPositionsAndRelatedAuthorities(String email);

    /**
     * Method that gets information about login employee`s positions.
     *
     * @param email {@link String} - employee email.
     * @return List of {@link String} - list of employee positions.
     *
     * @author Anton Bondar
     */
    List<String> getEmployeeLoginPositionNames(String email);

    /**
     * Method that gets information about login employee`s positions.
     *
     * @return List of {@link String} - list of employee positions.
     *
     * @author Anton Bondar
     */
    List<String> getEmployeeLoginPositionNames();
}
