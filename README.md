# MoviesApp (Part 2)
Sequel to the MoviesApp previously completed: An Android application that displays movie information and details. This project is part of the submission for the Udacity Android Developer Nanodegree course.

This app retrives a list of movies sorted by popularity or rating. Users may toggle the sort order through an Action Bar option. The app will, by default, load movies by popularity. If the user changes the sort order at anytime, this will be saved as the user's preference, and the list of movies will be loaded according to the user's preference once the app is launched.

## Additional Functionality
Users may select a movie's thumbnail to see more details about the movie. This includes details such as title, rating, overview, and release date.

## Technical Information
The app makes use of `The Movie Databse` API. Please see the following link for more information about this API: https://developers.themoviedb.org/3/movies

NB: Note that for developers, you need to obtain an API Key from `The Movie Database` which will be used to make API calls.
Once you have obtained an API key, place your key in the following file before attempting to build/run the application:
`${PROJECT_ROOT_FOLDER}/app/src/main/res/values/strings.xml`. In this file, you will see a placeholder `${REPLACE_THIS_WITH_YOUR_API_KEY}`. Replace this placeholder with your API Key.

## Acknowledgements
As stated under Technical Information, this app retrieves data from `The Movie Database`. The developer wishes to thank them for making their API available for use as part of this project submission.
