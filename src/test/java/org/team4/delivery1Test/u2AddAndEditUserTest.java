package org.team4.delivery1Test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.team4.common.Settings;
import org.team4.user.User;
import org.team4.user.UserService;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(User.class)
class u2AddAndEditUserTest {
    public User mockUser = new User(
            "test",
            "testFamily",
            12
    );

    public User mockUsers = new User("foo", "faa", 12);

    @Test
    public void test() {
        Assert.assertEquals(1,1);
    }

    @Test
    public void testGetters() {
        String name = mockUsers.getName();
        String status = mockUsers.getStatus();
        int age = mockUsers.getAge();
        int x = mockUsers.getX();
        int y = mockUsers.getY();
        Assert.assertEquals(name, "foo");
        Assert.assertEquals(status, "faa");
        Assert.assertEquals(age, 12);
        Assert.assertEquals(x, 0);
        Assert.assertEquals(y, 0);
    }

    @Test
    public void testIsAdult() {
        Assert.assertFalse(mockUsers.isAdult());
    }

    @Test
    public void testGetUser_notFound() throws IOException {
        ArrayList<User> mockList = new ArrayList<>();
        PowerMockito.mockStatic(User.class);
        when(User.getAllUsers()).thenReturn(mockList);
        User result = User.getUser("foo");
        Assert.assertEquals(null, result);
    }

    @Test
    public void getAllUsersTest_ReturnArrayList() throws IOException {
        ArrayList<User> mockReturn = new ArrayList<User>();
        mockReturn.add(mockUsers);

        UserService mockUser = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.getAllUsers()).thenReturn(mockReturn);
        ArrayList<User> result = mockUser.getAllUsersList();
        Assert.assertEquals(mockReturn, result);

    }

    @Test
    public void getAllUsersTest_ReturnNull() throws IOException {
        UserService mockUser = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.getAllUsers()).thenThrow(new IOException());
        ArrayList<User> result = mockUser.getAllUsersList();
        Assert.assertEquals(null, result);
    }

    @Test
    public void validateNameTest_valid() throws IOException {
        String name = "test";
        UserService mockUser = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.userExist(name)).thenReturn(false);
        String result = mockUser.validateName(name,true);
        Assert.assertEquals(null, result);
    }

    @Test
    public void validateNameTest_invalid() throws IOException {
        String name = "test";
        UserService mockUser = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.userExist(name)).thenReturn(true);
        String result = mockUser.validateName(name,true);
        Assert.assertEquals("Already exists", result);
    }

    @Test
    public void validateAgeTest_valid() {
        String age = "1";
        UserService testService = new UserService();
        String result = testService.validateAge(age);
        Assert.assertEquals(null, result);
    }

    @Test
    public void validateAgeTest_invalid() {
        String age = "-1";
        UserService testService = new UserService();
        String result = testService.validateAge(age);
        Assert.assertEquals("Positive only\n", result);
    }

    @Test
    public void validateAgeTest_invalidNotInt() {
        String age = "abc";
        UserService testService = new UserService();
        String result = testService.validateAge(age);
        Assert.assertEquals("Integer only\n", result);
    }

    @Test
    public void addUserTest_fail() throws IOException {
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.addNewUsers(mockUser)).thenThrow(new IOException());
        boolean result = serviceMock.addUser("foo", "foo", 12);
        Assert.assertTrue(result);
    }

    @Test
    public void addUserTest_good() throws IOException {
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.addNewUsers(mockUser)).thenReturn(true);
        boolean result = serviceMock.addUser("foo", "foo", 12);
        Assert.assertTrue(result);
    }

    @Test
    public void getSingleUserTest_good() throws IOException {
        String name = "foo";
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.getUser(name)).thenReturn(mockUser);
        User result = serviceMock.getSingleUser("foo");
        Assert.assertEquals(mockUser, result);
    }

    @Test
    public void getSingleUserTest_fails() throws IOException {
        String name = "foo";
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        when(User.getUser(name)).thenThrow(new IOException());
        User result = serviceMock.getSingleUser("foo");
        Assert.assertEquals(null, result);
    }

    @Test
    public void deleteSingleUserTest_fails() throws IOException {
        String name = "foo";
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        Settings.currentUser = "foo";
        when(User.deleteUser(name)).thenThrow(new IOException());
        boolean result = serviceMock.deleteSingleUser("foo");
        Assert.assertFalse(result);
    }

    @Test
    public void deleteSingleUserTest_good() throws IOException {
        String name = "foo";
        UserService serviceMock = new UserService();
        PowerMockito.mockStatic(User.class);
        Settings.currentUser = "foo";
        when(User.deleteUser(name)).thenReturn(true);
        boolean result = serviceMock.deleteSingleUser("foo");
        Assert.assertTrue(result);
    }
}