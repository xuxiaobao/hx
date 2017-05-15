package com.miaosu.service.serialno;

import com.miaosu.mapper.SerialNoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by angus on 15/9/29.
 */
@Service
@Transactional(timeout = 10)
public class SerialNoService {
    @Autowired
    private SerialNoMapper serialNoMapper;

    public Long curVal(String seqName){
        return serialNoMapper.curVal(seqName);
    }

    public Long nextVal(String seqName){
        return serialNoMapper.nextVal(seqName);
    }
}
