package com.newproject;

import com.newproject.entity.Song;
import com.newproject.entity.User;
import com.newproject.mapper.UserMapper;
import com.newproject.service.Login;
import com.newproject.service.SongManager;
import com.newproject.service.UpdateDB;
import com.newproject.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
class LoginApplicationTests {
	@Autowired
	SongManager songManager;

	@Test
	void contextLoads() {
	}
	@Test
	public void test() {
		//step 1, get an instance of sqlsession
		SqlSession sqlSession = MybatisUtils.getSqlSession();
		//step 2, execute sql code
		UserMapper userMapper = sqlSession.getMapper(UserMapper.class); // we do this to get the Usermapper
		List<User> users = userMapper.getAllUser();
		for (User user : users) {
			System.out.println(user);
		}
		// close sqlsession
		sqlSession.close();
	}
	@Test
	public void testAddSong(){
		assert Objects.equals(songManager.addSong("Shivers",
				"https://www.youtube.com/watch?v=Il0S8BoucSA",
				"Ed Sheeran"), "Success!");
	}
	@Test
	public void testGetSong(){
		System.out.println(songManager.getSong("Shivers"));
	}
	@Test
	public void testRateSong(){
		songManager.rateSong(new Song(
				"https://www.youtube.com/watch?v=Il0S8BoucSA",
				"Ed Sheeran",
				"Shivers"), 5);
	}

	@Test
	public void getUserById(){
		UpdateDB updateDB = new UpdateDB();
		System.out.println(updateDB.getUserById("hz2002"));
	}
	//增改需要提交事物
	@Test
	public void addUser(){
		UpdateDB updateDB = new UpdateDB();
		updateDB.deleteUser("colin2002");
		assert Objects.equals(updateDB.addUser(
				"colin2002",
				"jason@gmail.com",
				"22/05/2001",
				"colin",
				"kdksdisdisj",
				"Huynh"), "Success!");
	}

	@Test
	public void updateUser1() {
		UpdateDB updateDB = new UpdateDB();
		assert Objects.equals(updateDB.updatePassword("jason2002", "idjsidjsdin"), "Success!");
	}

	@Test
	public void getSong(){

	}

	@Test
	public void updateEmail() {
		UpdateDB updateDB = new UpdateDB();
		assert Objects.equals(updateDB.updateEmail("newuser1", "newuser1@gmail.com"), "Success!");
	}

	@Test
	public void deleteUser() {
		UpdateDB updateDB = new UpdateDB();
		assert Objects.equals(updateDB.deleteUser("jz2002"), "User does not exist");

	}

	@Test
	public void login(){
		Login login = new Login();
		assert Objects.equals(login.login("jason2002", "idjsidjsdin"), true);
	}


}
