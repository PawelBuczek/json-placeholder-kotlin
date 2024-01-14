package org.pbuczek.post

class Post {
    var userId = 0
    var id = 0
    var title: String? = null
    var body: String? = null

    constructor()
    constructor(userId: Int, id: Int, title: String?, body: String?) {
        this.userId = userId
        this.id = id
        this.title = title
        this.body = body
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (userId != other.userId) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId
        result = 31 * result + id
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (body?.hashCode() ?: 0)
        return result
    }


}