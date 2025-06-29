# QuizMaster - Advanced Android Quiz Application



A sophisticated quiz application with admin/student roles, real-time Firebase integration, and advanced exam handling logic.



## 🔥 Key Features

### Core Functionality
- **Firebase Realtime Database** - JSON-based data structure for courses, questions, and results
- **Dual Interface** - Separate fragments for Admin and Student views
- **Exam System** - MCQ with penalty scoring and time limits
- **Custom Views** - To represent GPA and Course Statistics

### Advanced Logic
- ⏳ **Exam Eligibility Rules** - Multiple conditional checks before allowing attempts
- 📊 **GPA Calculation** - Automatic grade point computation
- ⚡ **Optimized Listeners** - Efficient handling of Firebase async operations

### Technical Implementation
- 🔐 Shared Preferences for session management
- 📝 ViewPager2 with RecyclerView for MCQ navigation
- 🧩 Fragments-based architecture
- 🚦 Custom listeners for exam state management

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| Database | Firebase Realtime DB |


## 📋 Requirements

1. Android Studio Flamingo (2022.2.1) or newer
2. JDK 17+
3. Firebase project with:
   - Realtime Database enabled
   - Authentication configured (Email/Password)
4. Minimum SDK: API 26 (Android 8.0)

## 🚀 Installation

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/quiz-app-android.git

### 2. Firebase Setup
	## 2.1 Create a new Firebase project
	## 2.2 Add Android app with package com.yourdomain.quizapp
	## 2.3 Download google-services.json and place in app/ folder
	## 2.4 Enable these Firebase services:
		# 2.4.1 Realtime Database (with rules shown below)
		# 2.4.2 Email/Password Authentication






  
