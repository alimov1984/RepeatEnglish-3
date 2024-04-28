package ru.alimov.repeatenglish.model

import java.time.Instant

/**
 * Class describes word inside app.
 */
class Word(_wordOriginal: String, _wordTranslated: String, _dateCreated: Instant,
                _dateUpdated: Instant, _dateShowed: Instant?, _addCounter: Long,
                _correctCheckCounter: Long, _incorrectCheckCounter: Long, _rating: Long) {
    var id: Long = 0
        private set

    val wordOriginal: String = _wordOriginal
      //  private set

    var wordTranslated: String = _wordTranslated
        private set

    var dateCreated: Instant = _dateCreated
        private set

    var dateUpdated: Instant = _dateUpdated

    var dateShowed: Instant? = _dateShowed
        private set

    var addCounter: Long = _addCounter

    var correctCheckCounter: Long = _correctCheckCounter

    var incorrectCheckCounter: Long = _incorrectCheckCounter

    var rating: Long = _rating

    constructor(
        _id: Long, _wordOriginal: String, _wordTranslated: String, _dateCreated: Instant,
        _dateUpdated: Instant, _dateShowed: Instant?, _updateCounter: Long,
        _correctCheckCounter: Long, _incorrectCheckCounter: Long, _rating: Long
    ) : this(
        _wordOriginal, _wordTranslated, _dateCreated, _dateUpdated, _dateShowed,
        _updateCounter, _correctCheckCounter, _incorrectCheckCounter, _rating
    ) {
        this.id = _id
    }

}