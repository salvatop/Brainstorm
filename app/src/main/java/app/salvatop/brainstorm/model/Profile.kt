package app.salvatop.brainstorm.model

import java.io.Serializable

class Profile : Serializable {
        lateinit var uid: String
        lateinit var city: String
        lateinit var motto: String
        lateinit var occupation: String
        lateinit var displayName: String
        lateinit var followed: HashMap<String, String>
        lateinit var following: HashMap<String, String>
        lateinit var teams: ArrayList<String>
        lateinit var ideas: HashMap<String, Idea>
        lateinit var bookmarks: HashMap<String, Idea>

    constructor()
    constructor(uid: String, city: String, motto: String, occupation: String, displayName: String, followed: HashMap<String, String>, following: HashMap<String, String>, teams: ArrayList<String>, ideas: HashMap<String, Idea>, bookmarks: HashMap<String, Idea>) {
        this.uid = uid
        this.city = city
        this.motto = motto
        this.occupation = occupation
        this.displayName = displayName
        this.followed = followed
        this.following = following
        this.teams = teams
        this.ideas = ideas
        this.bookmarks = bookmarks
    }


}