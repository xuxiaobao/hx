package com.miaosu.service.locks;

import com.miaosu.mapper.LockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * Lock Service
 * Created by angus on 15/10/7.
 */
@Service
public class LockService {

    private static Logger logger = LoggerFactory.getLogger(LockService.class);

    @Autowired
    private LockMapper lockMapper;

    public boolean acquireLock(String name) {

        String lockedBy = null;
        try {
               lockedBy = InetAddress.getLocalHost().getHostAddress();
        }catch(Exception ex){
            logger.warn("获取{}锁失败", name, ex);
        }
        return lockMapper.updateAcquireLock(name,lockedBy)==1;
    }

    public void releaseLock(String name) {
        try{
            lockMapper.updateReleaseLock(name);
        }catch(Exception ex){
            logger.warn("释放{}锁失败", name, ex);
        }
    }
}
