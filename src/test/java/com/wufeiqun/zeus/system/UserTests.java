package com.wufeiqun.zeus.system;

import cn.hutool.crypto.digest.DigestUtil;
import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.common.entity.SelectVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserTests {

	@Autowired
	private UserFacade userFacade;

	@Test
	void test() {
		List<SelectVO> list = userFacade.getSelectableUserList();
		System.out.println(list);
	}

}
