# kotlin-networking

This library isn't platform specific.

This library is a collection of extension functions for OkHttp that enables OkHttp to be used in a way similar to Retrofit without all of the annotation magic.  It also includes Gson for receiveConversion to and from JSON.

## Key functions and classes

`OkHttpApi` - Make your API object extends this to use headers common to entire API, as well as a common root.

`Request.Builder.lambdaGson<T>` - Uses your request builder to create a lambda that executes the request, with the response converted to `T` using Gson.  This is useful because it allows you to use `.invokeAsync{ response -> }` from [kotlin-core](https://github.com/lightningkite/kotlin-core).

`TypedResponse<T>` - A typed network response.  Important things it contains: `.isSuccessful()`, `.result`, `.errorString`, `.code`.

## Example usage

```kotlin
object ExampleAPI : OkHttpApi("https://jsonplaceholder.typicode.com") {

  //This will get from "https://jsonplaceholder.typicode.com/posts" with a return type of List<Post>
  //getPosts() returns a ()->List<Post>
  fun getPosts() = requestBuilder("/posts").get().lambdaGson<List<Post>>()
  
  //This will post `post` to "https://jsonplaceholder.typicode.com/posts" with a return type of Post
  fun createPost(post:Post) = requestBuilder("/posts").post(post).lambdaGson<Post>()
}

//The model.  Gson takes care of the matching of names to JSON keys.
class Post(
    var userId: Long = -1,
    var id: Long? = null,
    var title: String = "",
    var body: String = ""
)

fun test(){

  //synchronous
  val response = ExampleAPI.getPosts().invoke()
  if(response.isSuccessful()){
    println("Post list obtained.  Number of posts: ${response.result!!.size}")
  } else {
    println("There was an error. ${response.errorString}")
  }
  
  //asynchronous
  val post = Post(userId = 3, title = "New Post", body = "This is a new post!")
  ExampleApi.createPost(post).invokeAsync{ response ->
    if(response.isSuccessful()){
      println("Post creation was successful.  Post ID: ${response.result!!.id}")
    } else {
      println("There was an error. ${response.errorString}")
    }
  }
}
```
