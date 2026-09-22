package io.edusoliveira.meudia.data.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromBillCategory(value: BillCategory): String = value.name

    @TypeConverter
    fun toBillCategory(value: String): BillCategory = BillCategory.valueOf(value)

    @TypeConverter
    fun fromBillType(value: BillType): String = value.name

    @TypeConverter
    fun toBillType(value: String): BillType = BillType.valueOf(value)
}
