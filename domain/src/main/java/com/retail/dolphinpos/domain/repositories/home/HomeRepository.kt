package com.retail.dolphinpos.domain.repositories.home

import com.retail.dolphinpos.domain.model.home.catrgories_products.CategoryData
import com.retail.dolphinpos.domain.model.home.catrgories_products.Products
import com.retail.dolphinpos.domain.model.home.customer.Customer

interface HomeRepository {
    suspend fun getCategories(): List<CategoryData>
    suspend fun getProductsByCategoryID(categoryID: Int): List<Products>
    suspend fun searchProducts(query: String): List<Products>

    suspend fun insertCustomerDetailsIntoLocalDB(customer: Customer)

}