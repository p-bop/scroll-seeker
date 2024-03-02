# __📜 WELCOME TO OUR VIRTUAL SCROLL ACCESS SYSTEM 📜__

> Hey there reader, the above repository holds the code for a virtual scroll access system. It allows for a variety of cool functionalities a scroll management domain can adopt. To walk you through our work continue reading down belown

## Installation 🛠️
_ _ _ 
> This readme has been made to walk you through the entirety of gaining this code and either extending it or using it as it is. 

> **IMPORTANT**

1. Prelimary step: **Ensure you have java 17.0.8 or above and Gradle 8.2.1**
2. First step: **Clone the repository**
    - After your proceed to find the code on the main repository, copy the repository URL (either HTTPS or SSH) by clicking the icon
3. Second step: **Creating your directory**
    - On your local machine, using terminal navigate into your chosen directory ( this is where you store the code, ideally a folder) 
4. Third step: **Cloning the repository**
    - Once you have changed your path to desired place of storage, type in the following command, **git clone [directory path]**
5. Fourth step: **Authenticate**
    - You will be asked to fill in your details, then github will download the entire repository on your chosen repository
_ _ _

## Continuous Integration and Development 🖥️
> For ongoing automation and continuous monitoring throughout the development, you can use the following tools like I have.

- Jacoco - Helps measure code coverage by tests implemented and helps give a visual upon the test readability and code quality‌.

- Junit - Framework which helps write and run tests to explore written code and its coverage of functionalities.

- Jenkins - CI/CD tool which helps with automation of many tasks including testing.

- Github - The overall team management and code contribution can be managed using Github. A project board can be created where you can keep a track of backlog and create issues - which can be used to assign work to team-members. An ideal methodology is to divide the project into different sprints on the project board and assign different issues to each sprint, leading to efficient distribution of time whilst progressing through the project. Using the set of procedure and strategies mentioned, a project can be effeciently carried out resulting in primarily, the commitments being met, and furthermore, efficient delegation of tasks. 
_ _ _

## Features 💡
> There are a multitude of features implemented throughout this program, I recommend testing them out and exploring it yourself. Here is a quick brief explanation of major specifications followed and client specific requirements that I addressed. 

_ _ _

> Generic functions implemented 

1. Registration and Login: Users can create accounts with detailed profiles, including personal information such as their phone number, email address, full name and customisable ID keys. **UNIQUE CREDENTIALS IMPLEMENTED**
2. Update User Profiles: Logged-in users can update and change their information on their profile, including their password.
3. Guest Users: Users may anonymously use the application without logging in, but are only able to view scrolls and may not upload or download scrolls.
4. Logged in Users: Users include different functionalities but lesser in comparison to admin, a logged in user has the capability to upload and download scrolls. 
5. Admin Users: The admin account created by the team has the following credentials 
    - Username: username
    - Password: password
    - **DONT TELL ANYONE**  
6. Admins can:
    - View a list of all users and their profiles
    - Be able to add and delete other users
    - View stats such as the number of downloads/uploads for each scroll ever passed through the application.
7. Edge cases are handled in regards to improper input.
8. User Type Display: The user’s name and type are displayed on the main UI.
9. Password encryption: The password for login stored (either in a database or locally stored file) should be encrypted using any hashing algorithm.
10. Adding New Digital Scrolls: Users can add scrolls to the virtual library. Each scroll will have a unique name and ID for categorisation. When adding a scroll, the user will be asked to upload its binary file.
11. Edit and Update Digital Scrolls: Users can make modifications to the scrolls they have uploaded. Specification being they can only edit their own uploads.
12. Remove Digital Scrolls: Users can remove any scrolls that they have uploaded to the platform.
13. View Scrolls: Users can view all available scrolls. 
14. Download Scrolls: Users can pick a scroll to download anytime during their browsing. 
15. Search Filters: refined searches based on uploader ID, scroll ID, name and upload date are possible.
16. Preview Scrolls: All users can preview scrolls on the platform prior to downloading them.
_ _ _

> Client specified functions implemented 

1. Created a ‘log’ file that gets written to every single time a task is completed, such as ‘<user_name> uploaded a scroll’; for both admin and logged in users
2. Implemented a 'time travel' feature, allowing users to view previous versions of a scroll they are viewing.

_ _ _

## How you can contribute 🤝 
> Following are the steps if you wish to contribute. 

- Fork the repository – this will help create a copy of the repository on your personal github while you can make changes and edit according to your wish and the components you wish to add.

* Clone the repository – cloning creates a local copy on your device which you can use to push back to your forked repository on github. Follow the steps mentioned previously to clone our repository. You can use this and push back to your forked repository to finalise changes.

+ Create a new branch – Once you are convinced that your changes can add a beneficial amount of support to our existing code, create a branch on our repository using the command git checkout -b <your branches name> 

- Commit – Using git commit -m “message”, commit the changes you made, perform git add . (at the start prior to committing). This will help save the changes made. 

* Push – Penultimate step, push the code onto your branch using git push. 

+ Create a pull request – A pull request is to merge your repository to the original one, Once your ready fill out the information required to create a pull request ( found on your repository), and once done we will review the content. If the content is improving our functionality, we will approve the pull request and fix any merge conflicts that happen. 
_ _ _

## 🌟 Happy Coding! 🌟
_ _ _

![Goodbye pic](https://live-production.wcms.abc-cdn.net.au/1fb87517529a75879401babfa7fac4ef?impolicy=wcms_crop_resize&cropH=556&cropW=837&xPos=0&yPos=0&width=862&height=575)
