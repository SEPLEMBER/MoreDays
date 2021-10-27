package de.wuapps.moredays.services

//data class CSVData
//    @CsvBindByName(column = "title")
//    val title: String,
//    @CsvBindByName(column = "amount")
//    val amount: Double,
//    @CsvBindByName(column = "transactionType")
//    val transactionType: String,
//    @CsvBindByName(column = "tag")
//    val tag: String,
//    @CsvBindByName(column = "date")
//    val date: String,
//    @CsvBindByName(column = "note")
//    val note: String,
//    @CsvBindByName(column = "createdAt")
//    val createdAtDate: String
//) : Exportable
//
//fun List<>.toCsv() : List<TransactionsCSV> = map {
//    TransactionsCSV(
//        title = it.title,
//        amount = it.amount,
//        transactionType = it.transactionType,
//        tag = it.tag,
//        date = it.date,
//        note = it.note,
//        createdAtDate = it.createdAtDateFormat,
//    )
//}