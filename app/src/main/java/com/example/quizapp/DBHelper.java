package com.example.quizapp;

import android.util.Log;

import com.example.quizapp.classes.Constants;
import com.example.quizapp.classes.Course;
import com.example.quizapp.classes.Enrollment;
import com.example.quizapp.classes.Question;
import com.example.quizapp.classes.Student;
import com.example.quizapp.listeners.OnEnrolledCheckListener;
import com.example.quizapp.listeners.OnLoadedListener;
import com.example.quizapp.listeners.OnModifyListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DBHelper {//Checked
    public FirebaseFirestore db;

    public DBHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getAllCourses(OnLoadedListener<Course> listener) {//checked
        db.collection(Constants.COURSE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> courses = new ArrayList<>();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot snapshot : querySnapshot) {
                            Course course = snapshot.toObject(Course.class);
                            courses.add(course);
                        }
                    }
                    listener.onLoaded(courses);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void getCourseByCode(String courseCode, OnLoadedListener<Course> listener) {//Checked
        db.collection(Constants.COURSE)
                .document(courseCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Course> courses = new ArrayList<>();
                        Course course = documentSnapshot.toObject(Course.class);
                        courses.add(course);
                        listener.onLoaded(courses);
                    } else {
                        listener.onFailure(new Exception("Course not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void addCourse(Course course, OnModifyListener listener) {//Checked
        String courseCode = course.getCourseCode();

        db.collection(Constants.COURSE)
                .document(courseCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onAlreadyExists();
                    } else {
                        db.collection(Constants.COURSE)
                                .document(courseCode)
                                .set(course)
                                .addOnSuccessListener(unused -> listener.onSuccess())
                                .addOnFailureListener(listener::onFailure);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }


    public void getAllQuestionsPerCourse(Course course, OnLoadedListener<Question> listener) {//Checked
        db.collection(Constants.QUESTION)
                .whereEqualTo("courseCode", course.getCourseCode())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Question> questions = new ArrayList<>();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot snapshot : querySnapshot) {
                            Question question = snapshot.toObject(Question.class);
                            question.setCourse(course);
                            questions.add(question);
                        }
                    }
                    listener.onLoaded(questions);
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void addQuestion(Question question, OnModifyListener listener) {//Checked
        HashMap<String, Object> ques = new HashMap<>();
        ques.put("courseCode", question.getCourse().getCourseCode());
        ques.put("questionText", question.getQuestionText());
        ques.put("options", question.getOptions());
        ques.put("correctOptions", question.getCorrectOptions());

        db.collection(Constants.COUNTERS)
                .document("question")
                .get()
                .addOnSuccessListener(querySnapShot -> {
                    if (querySnapShot.exists()) {
                        Long lastIdLong = querySnapShot.getLong("lastId");
                        if (lastIdLong == null) {
                            listener.onFailure(new Exception("Invalid lastId in counter document"));
                            return;
                        }

                        int lastId = lastIdLong.intValue();
                        lastId++;
                        ques.put("questionId", lastId);
                        int finalLastId = lastId;

                        db.collection(Constants.QUESTION)
                                .document(String.valueOf(lastId))
                                .set(ques)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection(Constants.COUNTERS)
                                            .document("question")
                                            .update("lastId", finalLastId)
                                            .addOnSuccessListener(aVoid2 -> {
                                                listener.onSuccess();
                                            })
                                            .addOnFailureListener(e -> {
                                                listener.onFailure(e);
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    listener.onFailure(e);
                                });
                    } else {
                        listener.onFailure(new Exception("Counter question does not exist"));
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void getStudentsByCourse(Course course, OnLoadedListener<Enrollment> listener) {//Checked
        //getting List of enrolled students for a course
        List<Enrollment> enrollments = new ArrayList<>();

        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("courseCode", course.getCourseCode())
                .get()
                .addOnSuccessListener(enrollmentSnapshots -> {
                   // Log.d("ENROLLMENTS", enrollmentSnapshots.toString() + "");
                    if (enrollmentSnapshots.isEmpty()) {
                        listener.onLoaded(enrollments);
                        return;
                    }

                    int totalEnrollments = enrollmentSnapshots.size();
                    int[] completedCount = {0};

                    for (QueryDocumentSnapshot enrollmentDoc : enrollmentSnapshots) {
                        Long gradeTemp = enrollmentDoc.getLong("grade");
                        int grade = (gradeTemp != null) ? gradeTemp.intValue() : 0;
                        Long studentIdTemp = enrollmentDoc.getLong("studentId");
                        int studentId = studentIdTemp.intValue();
                        boolean passed = Boolean.TRUE.equals(enrollmentDoc.getBoolean("passed"));
                      //  Log.d("STUDENT_ID", "student id:" + studentId);

                        db.collection(Constants.STUDENT)
                                .whereEqualTo("fileNumber", studentId) //
                                .get()
                                .addOnSuccessListener(studentSnapshot -> {
                                    Log.d("ENROLLMENTS", studentSnapshot + "");
                                    if (!studentSnapshot.isEmpty()) {
                                        DocumentSnapshot studentDoc = studentSnapshot.getDocuments().get(0);
                                        Student student = studentDoc.toObject(Student.class);

                                        Enrollment enrollment = new Enrollment();
                                        enrollment.setStudent(student);
                                        enrollment.setGrade(grade);
                                        enrollment.setPassed(passed);
                                        enrollment.setCourse(course);
                                     //   Log.d("ENROLLEMNT", enrollment + "");

                                        enrollments.add(enrollment);
                                    }
                                    completedCount[0]++;
                                    if (completedCount[0] == totalEnrollments) {
                                        listener.onLoaded(enrollments);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    completedCount[0]++;
                                    if (completedCount[0] == totalEnrollments) {
                                        listener.onLoaded(enrollments);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void updateQuestion(Question question, OnModifyListener listener) {//Checked
        HashMap<String, Object> ques = new HashMap<>();
        ques.put("questionId", question.getQuestionId());
        ques.put("courseCode", question.getCourse().getCourseCode());
        ques.put("questionText", question.getQuestionText());
        ques.put("options", question.getOptions());
        ques.put("correctOptions", question.getCorrectOptions());

        db.collection(Constants.QUESTION)
                .document(String.valueOf(question.getQuestionId()))
                .update(ques)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void deleteQuestion(int questionId, OnModifyListener listener) {//Checked
        db.collection(Constants.QUESTION)
                .document(String.valueOf(questionId))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void updateCourse(Course course, OnModifyListener listener) {//Checked
        String courseCode = course.getCourseCode();

        db.collection(Constants.COURSE)
                .document(courseCode)
                .set(course)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public void hasEnrolledStudents(Course course, OnEnrolledCheckListener listener) {//Checked
        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("courseCode", course.getCourseCode())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean hasEnrolled = !queryDocumentSnapshots.isEmpty();
                    listener.onCheck(hasEnrolled);
                })
                .addOnFailureListener(listener::onFailure);
    }

//    public void deleteCourse(Course course, OnModifyListener listener) {//Checked
//        db.collection(Constants.COURSE)
//                .whereEqualTo("courseCode", course.getCourseCode())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            doc.getReference().delete()
//                                    .addOnSuccessListener(unused -> listener.onSuccess())
//                                    .addOnFailureListener(e -> {
//                                        listener.onFailure(e);
//                                    });
//                            break; // Assume courseCode is unique; delete only one
//                        }
//                    } else {
//                        listener.onFailure(new Exception("Course not found"));
//                    }
//                })
//                .addOnFailureListener(listener::onFailure);
//    }

    public void deleteCourse(Course course, OnModifyListener listener) {//Checked
        db.collection(Constants.QUESTION)
                .whereEqualTo("courseCode", course.getCourseCode())
                .get()
                .addOnSuccessListener(questionSnapshots -> {
                    if (!questionSnapshots.isEmpty()) {
                        final int[] remaining = {questionSnapshots.size()};

                        for (QueryDocumentSnapshot questionDoc : questionSnapshots) {
                            Long qid = questionDoc.getLong("questionId");
                            if (qid == null) {
                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    // Proceed to delete course after all valid deletions
                                    proceedToDeleteCourse(course, listener);
                                }
                                continue;
                            }

                            deleteQuestion(qid.intValue(), new OnModifyListener() {
                                @Override
                                public void onSuccess() {
                                    remaining[0]--;
                                    if (remaining[0] == 0) {
                                        // Now delete the course
                                        proceedToDeleteCourse(course, listener);
                                    }
                                }

                                @Override
                                public void onAlreadyExists() {

                                }

                                @Override
                                public void onFailure(Exception e) {
                                    listener.onFailure(e);
                                }
                            });
                        }
                    } else {
                        // No questions to delete, delete the course immediately
                        proceedToDeleteCourse(course, listener);
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    private void proceedToDeleteCourse(Course course, OnModifyListener listener) {//Checked
        db.collection(Constants.COURSE)
                .whereEqualTo("courseCode", course.getCourseCode())
                .get()
                .addOnSuccessListener(courseSnapshots -> {
                    if (!courseSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : courseSnapshots) {
                            doc.getReference().delete()
                                    .addOnSuccessListener(unused -> listener.onSuccess())
                                    .addOnFailureListener(e -> {
                                        listener.onFailure(e);
                                    });
                            break;
                        }
                    } else {
                        listener.onFailure(new Exception("Course not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
         });
    }

    public void getAllStudents(OnLoadedListener<Student> listener) {//Checked
        Map<String, Course> courseMap = new HashMap<>();
        List<Student> students = new ArrayList<>();
        //getting Course objects to fetch them for students depending on
        // their enrollment(List<Enrollments> enrollments  attribute of class Student)
        getAllCourses(new OnLoadedListener<Course>() {
            @Override
            public void onLoaded(List<Course> items) {
                for (Course course : items) {
                    courseMap.put(course.getCourseCode(), course);
                }

                db.collection(Constants.STUDENT)
                        .get()
                        .addOnSuccessListener(studentSnapshots  -> {
                            if (!studentSnapshots .isEmpty()) {
                                int totalStudents = studentSnapshots .size();
                                int[] completedCount = {0};

                                for (QueryDocumentSnapshot doc : studentSnapshots ) {
                                    Student student = doc.toObject(Student.class);
                                    List<Enrollment> enrollments = new ArrayList<>();

                                    db.collection(Constants.ENROLLMENT)
                                            .whereEqualTo("studentId", student.getFileNumber())
                                            .get()
                                            .addOnSuccessListener(enrollmentDocuments -> {
                                                for (QueryDocumentSnapshot queryDoc : enrollmentDocuments) {

                                                    Enrollment enrollment = new Enrollment();
                                                    Long gradeTemp = queryDoc.getLong("grade");
                                                    int grade = (gradeTemp != null) ? gradeTemp.intValue() : 0;
                                                    boolean passed = Boolean.TRUE.equals(queryDoc.getBoolean("passed"));
                                                    String courseCode = queryDoc.getString("courseCode");

                                                    if (courseMap.containsKey(courseCode))
                                                        enrollment.setCourse(courseMap.get(courseCode));
                                                    else enrollment.setCourse(null);
                                                    enrollment.setStudent(student);
                                                    enrollment.setGrade(grade);
                                                    enrollment.setPassed(passed);
                                                    enrollments.add(enrollment);
                                                }
                                                student.setEnrollments(enrollments);
                                                students.add(student);
                                                completedCount[0]++;
                                                if (completedCount[0] == totalStudents) {
                                                    listener.onLoaded(students);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                completedCount[0]++;
                                                if (completedCount[0] == totalStudents) {
                                                    listener.onLoaded(students);
                                                }
                                            });
                                }
                            }else listener.onLoaded(null);
                        })
                        .addOnFailureListener(e -> {
                            listener.onFailure(e);
                        });
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void updateAssignment(Enrollment enrollment, boolean toAssign,  OnModifyListener listener){//Checked
        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("studentId",enrollment.getStudent().getFileNumber())
                .whereEqualTo("courseCode", enrollment.getCourse().getCourseCode())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(toAssign){
                        if(queryDocumentSnapshots.isEmpty()){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("studentId", enrollment.getStudent().getFileNumber());
                            map.put("courseCode", enrollment.getCourse().getCourseCode());
                            map.put("grade",enrollment.getGrade());
                            map.put("passed",enrollment.isPassed());
                            db.collection(Constants.ENROLLMENT)
                                    .add(map)
                                    .addOnSuccessListener(documentReference -> listener.onSuccess())
                                    .addOnFailureListener(e->listener.onFailure(e));
                        }
                    }else{
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            doc.getReference().delete();
                        }
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e->listener.onFailure(e));
    }

    public void getCourseHistoInfo(Course course, OnLoadedListener listener){//Checked
        //Log.d("COURSE",course+"");
        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("courseCode", course.getCourseCode())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Enrollment> enrollments = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDoc : queryDocumentSnapshots) {

                        Enrollment enrollment = new Enrollment();
                        Long gradeTemp = queryDoc.getLong("grade");
                        int grade = (gradeTemp != null) ? gradeTemp.intValue() : 0;
                        boolean passed = Boolean.TRUE.equals(queryDoc.getBoolean("passed"));
                        enrollment.setCourse(course);
                        enrollment.setStudent(null);
                        enrollment.setGrade(grade);
                        enrollment.setPassed(passed);
                        enrollments.add(enrollment);
                    }
                    listener.onLoaded(enrollments);
                    Log.d("TEST", "ENrollemnts fetched succ.");
                })
                .addOnFailureListener(e->listener.onFailure(e));
    }

    /*--------------------------------------ADDED (OMAR)--------------------------------------------------*/


    public void deleteStudent(Student student, OnModifyListener listener) {//Checked
        db.collection(Constants.STUDENT)
                .document(String.valueOf(student.getFileNumber()))
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }



    public void addStudent(Student student, OnModifyListener listener) {//Checked
        // Step 1: Check if student with same email already exists
        db.collection(Constants.STUDENT)
                .whereEqualTo("email", student.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        listener.onAlreadyExists("A student with this email already exists");
                    } else {
                        // Step 2: Get last student ID
                        db.collection(Constants.COUNTERS)
                                .document("students")
                                .get()
                                .addOnSuccessListener(counterSnapshot -> {
                                    Long lastId = counterSnapshot.getLong("lastId");
                                    if (lastId == null) {
                                        listener.onFailure(new Exception("Invalid lastId counter"));
                                        return;
                                    }

                                    int newFileNumber = lastId.intValue() + 1;
                                    student.setFileNumber(newFileNumber);
                                    student.generateCredentials(newFileNumber);

                                    // Step 3: Add student to DB
                                    db.collection(Constants.STUDENT)
                                            .document(String.valueOf(newFileNumber))
                                            .set(student)
                                            .addOnSuccessListener(unused -> {
                                                db.collection(Constants.COUNTERS)
                                                        .document("students")
                                                        .update("lastId", newFileNumber)
                                                        .addOnSuccessListener(unused2 -> listener.onSuccess())
                                                        .addOnFailureListener(listener::onFailure);
                                            })
                                            .addOnFailureListener(listener::onFailure);
                                })
                                .addOnFailureListener(listener::onFailure);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }






    public void updateStudent(Student student, OnModifyListener listener) {//Checked
        AtomicBoolean sameUsername = new AtomicBoolean(false);
        AtomicBoolean sameEmail = new AtomicBoolean(false);
       // AtomicBoolean sameFullName = new AtomicBoolean(false);
        db.collection(Constants.STUDENT)//getting students to verify no overlap in the updated fields
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean exists = false;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Student s = doc.toObject(Student.class);
                        int fileNumber = Integer.parseInt(doc.getId());

                        if (fileNumber != student.getFileNumber()) {
                            //if different student, make sure credentials modified does not overlap
                            sameEmail.set(s.getEmail().equalsIgnoreCase(student.getEmail()));
//                            sameFullName.set(s.getFirstName().equalsIgnoreCase(student.getFirstName()) &&
//                                    s.getLastName().equalsIgnoreCase(student.getLastName()));
                            sameUsername.set(s.getUsername().equalsIgnoreCase(student.getUsername()));

                            if (sameEmail.get() ||  sameUsername.get()) {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (exists) {
                        String msg="";
                        if(sameUsername.get())  msg = "A student with this username already exists";
                       // if(sameFullName.get())  msg = "Student with this full name already exists";
                        if(sameEmail.get())  msg = "Student with this email already exists";
                        listener.onAlreadyExists(msg);
                    } else {
                        db.collection(Constants.STUDENT)
                                .document(String.valueOf(student.getFileNumber()))
                                .set(student)
                                .addOnSuccessListener(unused -> listener.onSuccess())
                                .addOnFailureListener(listener::onFailure);
                    }
                })
                .addOnFailureListener(listener::onFailure);//method reference
        //equivalent to (e) -> listener.onFailure(e)
    }



    public void getStudentByFileNumber(int fileNumber, OnLoadedListener<Student> listener) {//Checked
        db.collection(Constants.STUDENT)
                .document(String.valueOf(fileNumber))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Student> students = new ArrayList<>();
                        Student student = documentSnapshot.toObject(Student.class);
                        students.add(student);
                        listener.onLoaded(students);
                    } else {
                        listener.onFailure(new Exception("Student not found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getStudentByUsername(String username, OnLoadedListener<Student> listener) {//checked
        db.collection(Constants.STUDENT)
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Student> students = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Student student = doc.toObject(Student.class);
                        students.add(student);
                    }

                    listener.onLoaded(students);
                })
                .addOnFailureListener(listener::onFailure);
    }





    public void getEnrollmentsByStudentId(int studentId, OnLoadedListener<Enrollment> listener) {//Checked
        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Enrollment> results = new ArrayList<>();
                    if (snapshot.isEmpty()) {
                        listener.onLoaded(results); // No enrollments
                        return;
                    }

                    List<Task<DocumentSnapshot>> courseTasks = new ArrayList<>();
                    //Task used bcz multiple async calls are occuring and we need to wait for them
                    //all before the listener.onLoaded() call
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String courseCode = doc.getString("courseCode");
                        double grade = doc.getDouble("grade");
                        boolean passed = doc.getBoolean("passed");

                        // Fetch the lastExamEndDate field
                        Date lastExamEndDate;
                        Timestamp ts = doc.getTimestamp("lastExamEndDate");
                        if (ts != null) {
                            lastExamEndDate = ts.toDate();
                        } else {
                            lastExamEndDate = null;
                        }

                        Task<DocumentSnapshot> courseTask = db.collection(Constants.COURSE)
                                .document(courseCode)
                                .get()
                                .addOnSuccessListener(courseDoc -> {
                                    if (courseDoc.exists()) {
                                        Course course = courseDoc.toObject(Course.class);
                                        Student stubStudent = new Student();
                                        stubStudent.setFileNumber(studentId);

                                        Enrollment enrollment = new Enrollment(course, stubStudent, grade, passed);
                                        enrollment.setLastExamEndDate(lastExamEndDate); //  Set it here
                                        results.add(enrollment);
                                    }
                                });

                        courseTasks.add(courseTask);
                    }

                    // Wait for all course fetches to finish
                    Tasks.whenAllComplete(courseTasks).addOnSuccessListener(tasks -> {
                        listener.onLoaded(results);
                    }).addOnFailureListener(listener::onFailure);

                })
                .addOnFailureListener(listener::onFailure);
    }





    public void updateEnrollment(Enrollment enrollment, OnLoadedListener<Boolean> listener) {//Checked
        db.collection(Constants.ENROLLMENT)
                .whereEqualTo("studentId", enrollment.getStudent().getFileNumber())
                .whereEqualTo("courseCode", enrollment.getCourse().getCourseCode())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String docId = snapshot.getDocuments().get(0).getId();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("grade", enrollment.getGrade());
                        updates.put("passed", enrollment.isPassed());
                        if (enrollment.getLastExamEndDate() != null) {
                            updates.put("lastExamEndDate", enrollment.getLastExamEndDate());
                        }

                        db.collection(Constants.ENROLLMENT)
                                .document(docId)
                                .update(updates)
                                .addOnSuccessListener(unused -> listener.onLoaded(List.of(true)))
                                .addOnFailureListener(listener::onFailure);
                    } else {
                        listener.onFailure(new Exception("Enrollment not found."));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }



}
