package com.example.android.iscrape

import android.util.Log
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*

fun main(args: Array<String>){
    println("Hello Kotlin")
    var test = arrayOf("fullName","userName","followerCount","followingCount","profileBio","profileWebsiteUrl","businessAccount","jointRecently","businessCategory","verifiedAccount")
    val wb: Workbook = HSSFWorkbook()
    var cell: Cell? = null
    val cellStyle: CellStyle = wb.createCellStyle()
    cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index)
    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND)

    //Now we are creating sheet

    //Now we are creating sheet
    var sheet: Sheet? = null
    sheet = wb.createSheet("Instagram Data")
    //Now column and row
    //Now column and row

    val row: Row = sheet.createRow(0)

    var tags = arrayOf("Name","Age","Stack")
    for (i in test){
        cell = row.createCell(test.indexOf(i))
        cell.setCellValue(i)
        cell.setCellStyle(cellStyle)
        println("TEST : index = ${test.indexOf(i)}")
        println("TEST : tag = $i")
    }

    sheet.setColumnWidth(0, 10 * 200)
    sheet.setColumnWidth(1, 10 * 200)
}