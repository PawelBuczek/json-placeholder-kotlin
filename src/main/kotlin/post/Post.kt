package post

import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor

@Suppress("unused")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
class Post {
    var userId = 0
    var id = 0
    var title: String? = null
    var body: String? = null
}