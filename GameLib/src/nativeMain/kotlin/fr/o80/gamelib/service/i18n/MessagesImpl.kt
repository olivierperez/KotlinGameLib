package fr.o80.gamelib.service.i18n

class MessagesImpl : Messages {

    // TODO OPZ
    // private val bundle = ResourceBundle.getBundle("i18n", Utf8Control())

    override fun get(key: String): String {
        return "TODO" // bundle.getString(key)
    }

    override fun get(key: String, vararg args: Any): String {
        return "TODO" // String.format(get(key), *args)
    }
}
