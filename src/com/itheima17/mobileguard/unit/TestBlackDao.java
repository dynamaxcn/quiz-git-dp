package com.itheima17.mobileguard.unit;

import java.io.FileNotFoundException;
import java.util.List;

import com.itheima17.mobileguard.dao.AntiVirusDao;
import com.itheima17.mobileguard.dao.BlackDao;
import com.itheima17.mobileguard.dao.ContactsDao;
import com.itheima17.mobileguard.dao.LockDao;
import com.itheima17.mobileguard.dao.PhoneLocationDao;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.domain.BlackBean;
import com.itheima17.mobileguard.domain.CommonBean;
import com.itheima17.mobileguard.domain.CommonTagBean;
import com.itheima17.mobileguard.domain.ContactBean;
import com.itheima17.mobileguard.utils.AppUtils;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.ServiceUtils;
import com.itheima17.mobileguard.utils.SmsUtils;
import com.itheima17.mobileguard.utils.SmsUtils.SDNotExistException;
import com.itheima17.mobileguard.utils.SmsUtils.SDNotGouException;
import com.itheima17.mobileguard.utils.TaskUtils;

import android.test.AndroidTestCase;

public class TestBlackDao extends AndroidTestCase {
	
	
	public void testLock(){
		LockDao dao = new LockDao(getContext());
		dao.addLock("aaa");
		dao.addLock("bbb");
		System.out.println(dao.getAllLockPacks());
		dao.removeLock("bbb");
		System.out.println(dao.getAllLockPacks());
	}
	public void testMem(){
		boolean virus = AntiVirusDao.isVirus("bbd1324001acff2523c7f9ab6c29b8a911");
		System.out.println(virus);
		/*long freeMem = TaskUtils.getFreeMem(getContext());
		long totalMem = TaskUtils.getTotalMem(getContext());
		PrintLog.print(freeMem + "<>" + totalMem);*/
		//System.out.println(TaskUtils.getAllRunningTaskInfos(getContext()));
	}
	public void testAdd(){
		BlackDao mBlackDao = new BlackDao(getContext());
		BlackBean bean = new BlackBean();
		for(int i = 0; i < 50; i++) {
			bean.setModel(1);
			//修改
			bean.setNumber("110" + i);
			mBlackDao.add(bean);
		}
		
		
		/*BlackBean bean = new BlackBean();
		bean.setModel(1);
		//修改
		bean.setNumber("12345680");
		mBlackDao.update(bean);
		
		//删除
		bean.setNumber("12345681");
		mBlackDao.remove(bean);
		//查询打印
		List<BlackBean> allDatas = mBlackDao.getAllDatas();
		System.out.println(allDatas);*/
		
	}
	
	
}
