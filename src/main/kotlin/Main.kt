import java.time.LocalDate

interface LibraryActions {
    fun addBook(book: Book)
    fun registerUser(user: User)
    fun findUser(userId: String): User?
    fun borrowBook(userId: String, isbn: String): Boolean
    fun returnBook(userId: String, isbn: String): Boolean
    fun searchBooks(text: String): List<Book>
}


data class Book(
    val title: String, val author: String, val isbn: String, val genre: String, var isAvailable: Boolean = true
)


abstract class User(
    val id: String, val name: String
) {
    private val borrowedBooks = mutableListOf<Book>()

    abstract val maxBooks: Int

    fun borrow(book: Book): Boolean {
        if (borrowedBooks.size >= maxBooks) return false

        borrowedBooks.add(book)
        return true
    }

    fun returnBook(book: Book) {
        borrowedBooks.remove(book)
    }

    fun getBooks(): List<Book> = borrowedBooks
}

class Student(
    id: String, name: String
) : User(id, name) {

    override val maxBooks = 3
}

class Teacher(
    id: String, name: String
) : User(id, name) {

    override val maxBooks = 10
}

class Library : LibraryActions {

    private val books = HashMap<String, Book>()
    private val users = HashMap<String, User>()
    private val history = mutableListOf<String>()
    private val genres = mutableSetOf<String>()

    override fun addBook(book: Book) {
        books[book.isbn] = book
        genres.add(book.genre)
    }

    override fun registerUser(user: User) {
        users[user.id] = user
    }

    override fun findUser(userId: String): User? {
        return users[userId]
    }

    override fun borrowBook(
        userId: String, isbn: String
    ): Boolean {
        val user = users[userId] ?: return false
        val book = books[isbn] ?: return false

        if (!book.isAvailable) return false
        if (!user.borrow(book)) return false

        book.isAvailable = false
        history.add(
            "${user.name} borrowed ${book.title} on ${LocalDate.now()}"
        )

        return true
    }


    override fun returnBook(
        userId: String, isbn: String
    ): Boolean {
        val user = users[userId] ?: return false
        val book = books[isbn] ?: return false

        user.returnBook(book)
        book.isAvailable = true
        history.add(
            "${user.name} returned ${book.title} on ${LocalDate.now()}"
        )

        return true
    }


    override fun searchBooks(text: String): List<Book> {
        return books.values.filter {
            it.title.contains(text, true) || it.author.contains(text, true)
        }
    }


    fun showStatistics() {
        println("Books: ${books.size}")
        println("Users: ${users.size}")
        println("Genres: $genres")
        println("\nHistory:")
        history.forEach(::println)
    }
}


fun main() {
    val library = Library()

    library.addBook(
        Book(
            "Clean Code", "Robert Martin", "001", "Programming"
        )
    )
    library.addBook(
        Book(
            "Kotlin in Action", "Dmitry Jemerov", "002", "Programming"
        )
    )

    val student = Student(
        "S1", "Alex"
    )
    val teacher = Teacher(
        "T1", "John"
    )

    library.registerUser(student)
    library.registerUser(teacher)

    println("Borrow book:")
    println(
        library.borrowBook("S1", "001")
    )

    println("\nStudent books:")
    library.findUser("S1")?.getBooks()?.forEach {
        println(it.title)
    }

    println("\nSearch:")
    library.searchBooks("Kotlin").forEach {
        println(it.title)
    }

    println("\nReturn book:")
    println(
        library.returnBook("S1", "001")
    )

    println()
    library.showStatistics()
}