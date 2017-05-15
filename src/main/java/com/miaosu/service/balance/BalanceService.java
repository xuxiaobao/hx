package com.miaosu.service.balance;

import com.miaosu.mapper.BalanceMapper;
import com.miaosu.model.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Balance service
 * Created by angus on 15/10/4.
 */
@Service
public class BalanceService {

    @Autowired
    private BalanceMapper balanceRepository;

    public Balance get(String userName) {
        return balanceRepository.selectByName(userName);
    }
}
