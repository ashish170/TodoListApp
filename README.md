
# TodoListApp
Major Highlights

* MVVM Architecture
* Kotlin
* Hilt
* Coroutines
* Flow
* StateFlow
The app has following packages:

* data: It contains all the data accessing and manipulating components.
* di: It contains all the dependency injection related classes and interfaces.
* ui: View classes along with their corresponding ViewModel.
* utils: Utility classes.

Store data in Room Database in the app  
Display a list of tasks from the database  
Allow user to delete a task 
Select a single task and show details  
Change the mode in settings screen  
## Setup Instructions

### Prerequisites

- **Android Studio**: Install the latest version of Android Studio (preferably **Giraffe** or newer).
- **Android SDK**: Ensure that you have the latest Android SDK and build tools installed.
- **Kotlin 2.0**: This project uses Kotlin 2.0. Make sure your environment is configured accordingly.
- **Room**: The app uses Room for local data persistence.
- **Hilt**: Dependency injection is handled with Hilt.
- **Jetpack Compose**: The UI is fully implemented using Jetpack Compose.


### Design Rationale

#### UI/UX
- **Jetpack Compose**: I used Jetpack Compose to build the entire user interface. It’s a modern way to create responsive and flexible designs that can easily change based on what’s happening in the app.
- **List and Detail Views**: The app has a main screen with a list of tasks. When you tap on a task, it opens a task screen with the same details typed in.
- **Dark Mode Support**: The app looks good in both light and dark modes. This helps with accessibility and makes the app look nice in different lighting conditions.
- **Animations**: I added smooth animations for things like switching between screens and interacting with tasks. These animations make the app feel more polished without being too distracting.

#### Architecture
- **MVVM (Model-View-ViewModel)**:
  - **Model**: This part handles the task data stores it in the Room database.
  - **ViewModel**: This acts like a bridge between the UI and the data. It takes care of all the logic, runs background tasks, and provides data to the UI.
  - **View**: These are the Composable functions that make up the UI. They get data from the ViewModel and display it to the user.


 #### Schema Diagram

 

![Schema](https://github.com/user-attachments/assets/98567fa6-2422-4281-9a53-1bade267fd3f)
