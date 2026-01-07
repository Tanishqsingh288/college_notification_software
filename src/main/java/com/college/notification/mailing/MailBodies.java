package com.college.notification.mailing;

public class MailBodies {

    public static String welcome(String name) {
        return """
                Hello %s,

                Welcome to the College Notification System 🎉

                Your account has been successfully created.
                You can now log in and stay updated with important
                college announcements and notifications.

                If you did not register for this account,
                please contact the college administration immediately.

                Regards,
                College Notification System
                """.formatted(name);
    }

    public static String passwordResetRequested() {
        return """
                Hello,

                We received a request to reset your password.

                If you initiated this request, please proceed
                to set a new password immediately.

                If you did NOT request this, please ignore
                this email or contact support.

                Regards,
                College Notification System
                """;
    }

    public static String passwordResetSuccess() {
        return """
                Hello,

                Your password has been successfully reset ✅

                You can now log in using your new password.
                If this was not done by you, please contact
                the administrator immediately.

                Stay secure,
                College Notification System
                """;
    }

    public static String loginAlert() {
        return """
                Hello,

                You have successfully logged in to your account.

                If this login was not performed by you,
                please reset your password immediately.

                Regards,
                College Notification System
                """;
    }
}
