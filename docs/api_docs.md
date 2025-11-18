
---

## `API_Documentation_Centralized_College_Notification_System.md`**

```markdown
# REST API Documentation - Centralized College Notification System

1. Authentication APIs
Method	Endpoint	Description
POST	/auth/register	Register new user (student/teacher)
POST	/auth/login	Authenticate user and return JWT token
GET	/auth/me	Get current logged-in user details
POST	/auth/change-password	Change password for logged-in user
2. Notice Management APIs
Method	Endpoint	Description
GET	/api/notices	Get list of all notices
GET	/api/notices/{id}	Get details of a specific notice
POST	/api/notices	Create new notice (Teacher only)
PUT	/api/notices/{id}	Update existing notice (Teacher only)
DELETE	/api/notices/{id}	Delete a notice (Teacher only)
POST	/api/notices/{id}/attachments	Upload attachment to a notice
GET	/api/attachments/{id}	Download attachment by ID
3. Student Interaction APIs
Method	Endpoint	Description
POST	/api/notices/{id}/view	Mark notice as viewed by student
GET	/api/notices/recent	Fetch recent notices for quick view
GET	/api/notices/search?query=exam	Search notices by title/content
4. Administrative & Utility APIs
Method	Endpoint	Description
GET	/api/stats/summary	Get summary (counts of users, notices)
GET	/api/departments	Fetch all departments
GET	/api/users/teachers	List all teachers
GET	/api/users/students	List all students
---

## Example Request - Register User
```json
POST /auth/register
{
  "fullName": "Ravi Kumar",
  "email": "ravi@college.edu",
  "password": "secret123",
  "role": "TEACHER",
  "departmentId": 1
}
