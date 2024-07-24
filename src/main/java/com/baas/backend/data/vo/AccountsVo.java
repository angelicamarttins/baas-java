package com.baas.backend.data.vo;

import com.baas.backend.data.dto.AccountDto;

public record AccountsVo(AccountDto.Response sourceAccount, AccountDto.Response targetAccount) {
}
