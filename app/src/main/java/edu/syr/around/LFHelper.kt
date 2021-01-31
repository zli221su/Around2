package edu.syr.around

object LFHelper {
    fun parseString(s : String) : List<String> {
        return s.split('|')
    }

    fun isIdExist(s : String, id : String) : Boolean {
        return s.indexOf(id) >= 0
    }

    fun addId(s : String, id : String) : String {
        if (isIdExist(s, id)) {
            return s
        }
        return s + id + "|"
    }

    fun removeId(s : String, id : String) : String {
        return s.replace(id + "|", "")
    }

    fun getIdCount(s : String) : Int {
        return parseString(s).size - 1
    }
}