package id.my.schedule;

import id.my.schedule.repository.ScheduleRepository;
import id.my.schedule.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScheduleTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void test(){
//        scheduleRepository.deleteAll();
//        userRepository.deleteAll();
//
//        User user = new User();
//        user.setId(UUID.randomUUID().toString());
//        user.setNickname("who");
//        user.setPassword(BCrypt.hashpw("Sandal1.", BCrypt.gensalt()));
//        user.setRole(UserRole.DEVELOPER);
//        userRepository.save(user);
    }

}
