package greencity.security.service;

import greencity.dto.security.UnblockTestersAccountDto;

public interface TestersUnblockAccountService {
    /**
     * Unblocks tester account by provided token.
     *
     * @param dto {@link UnblockTestersAccountDto} with token for unblocking tester
     *            account.
     */
    void unblockAccount(UnblockTestersAccountDto dto);
}
