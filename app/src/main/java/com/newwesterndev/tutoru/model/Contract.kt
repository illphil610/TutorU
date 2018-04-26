package com.newwesterndev.tutoru.model

object Contract {
    @JvmStatic val HELP_BROADCAST: String = "HelpBroadcast"
    @JvmStatic val DB_FIRST_APP_LAUNCH: String = "first_app_launch"
    @JvmStatic val APP_LAUNCHED: String = "appLaunched"
    @JvmStatic val APP_HASNT_LAUNCHED: String = "appHasntLaunchedYet"
    @JvmStatic val TUTEE: String = "Tutee"
    @JvmStatic val TUTOR: String = "Tutor"
    @JvmStatic val SHARED_PREF_SUBJECTS = "com.newwesterndev.TutorU.prefsSubjects"
    @JvmStatic val SHARED_PREF_COURSES = "com.newwesterndev.TutorU.prefsCourses"
    @JvmStatic val REQUESTING_HELP = "RequestingHelp"
    @JvmStatic val AVAILABLE_TUTORS = "AvailableTutors"
}