package com.college.notification.service;

import com.college.notification.entity.Teacher;
import com.college.notification.entity.User;
import com.college.notification.repository.TeacherRepository;
import com.college.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    /**
     * Update the admin status of a teacher's associated user account
     * @param teacherId ID of the teacher
     * @param isAdmin true to make admin, false to revoke
     * @return updated User object
     */
    public User updateAdminStatus(Long teacherId, boolean isAdmin) {
        // 1️⃣ Get the teacher
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // 2️⃣ Get the linked user
        User user = userRepository.findById(Long.valueOf(teacher.getUid()))
                .orElseThrow(() -> new RuntimeException("User not found for this teacher"));

        // 3️⃣ Update admin status
        user.setIsAdmin(isAdmin);

        // 4️⃣ Save user
        return userRepository.save(user);
    }
}
