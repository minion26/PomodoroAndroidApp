# 🍅 Pomodoro App 🍅

## Task Description
The task is to implement a Pomodoro timer application that helps users manage their time using the Pomodoro Technique. 
The application allows users to set a timer for work sessions and breaks, providing notifications and visual cues to enhance productivity.

## Explanation of the Solved Task

### 1. Introduction
The Pomodoro Technique is a time management method that uses a timer to break work into intervals, traditionally 25 minutes in length, separated by short breaks. 
This application helps users follow this technique by providing a customizable timer, notifications, and visual cues.

### 2. Project Structure
- **MainActivity:**
  - The main activity of the application where the timer is set and controlled.
  - Users can increase or decrease the timer duration and start or stop the timer.

- **Timer Functionality:**
  - **Work Session:** The default work session is set to 30 minutes, but users can adjust it.
  - **Break Session:** After each work session, a break session is triggered, with a default duration of 5 minutes.

- **Notifications and Visual Cues:**
  - **Sound Notification:** A sound is played at the end of each work session to signal the start of a break.
  - **Visual Cues:** The application changes images to simulate a growing flower as the timer progresses.

1. **Timer Implementation:**
   - The timer is implemented using `CountDownTimer` to handle the countdown for both work and break sessions.
   - The timer updates the UI every second to show the remaining time.

2. **Popup Windows:**
   - **Pause Popup:** A popup window appears at the end of each work session to notify the user of the break.
   - **Exit Confirmation:** A popup window appears when the user attempts to exit the app while the timer is running.

3. **User Interface:**
   - The UI includes buttons to increase or decrease the timer, a start button, and a text view to display the remaining time.
   - A toolbar is used as the action bar, with options to access settings and block apps during the timer.

### 3. Pomodoro Technique

| Session | Duration (minutes) |
|---------|--------------------|
| Work    | 25  -  120              |
| Break   | 5                  |


### 4. Next to do 
- Implement a feature to block other apps that the user can access while the timer is running to prevent distractions.

### 5. Conclusion
The Pomodoro App helps users manage their time effectively by implementing the Pomodoro Technique. 
The application provides a customizable timer, notifications, and visual cues to enhance productivity. 
The use of `CountDownTimer` ensures accurate timing, while popup windows and sound notifications keep the user informed and engaged.
