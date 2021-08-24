package com.tinkoffsirius.koshelok.repository

import android.content.SharedPreferences
import com.tinkoffsirius.koshelok.entities.Category
import com.tinkoffsirius.koshelok.entities.TransactionType
import com.tinkoffsirius.koshelok.repository.entities.CategoryData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewCategorySharedRepository(private val sharedPreferences: SharedPreferences) {

    fun saveNewCategory(categoryData: Category): Completable =
        Completable.fromCallable {
            val data = Json.encodeToString(categoryData)
            sharedPreferences.edit()
                .putString(NEW_CATEGORY_DATA, data)
                .apply()
        }

    fun getNewCategory(): Single<Category> = Single.fromCallable {
        val data = sharedPreferences.getString(NEW_CATEGORY_DATA , null)
        if(data!= null){
            Json.decodeFromString(Category.serializer() , data)
        } else{
            Category(0 , TransactionType.OUTCOME , "" , 0 , 0)
        }
    }

    fun removeNewCategory(){
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    companion object {
        const val NEW_CATEGORY_DATA = "newCategory"
    }
}
