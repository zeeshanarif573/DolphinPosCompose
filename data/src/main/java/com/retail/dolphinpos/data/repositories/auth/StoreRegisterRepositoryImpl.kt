package com.retail.dolphinpos.data.repositories.auth

import com.retail.dolphinpos.data.dao.ProductsDao
import com.retail.dolphinpos.data.dao.UserDao
import com.retail.dolphinpos.data.mapper.ProductMapper
import com.retail.dolphinpos.data.mapper.UserMapper
import com.retail.dolphinpos.data.service.ApiService
import com.retail.dolphinpos.domain.model.auth.login.response.Locations
import com.retail.dolphinpos.domain.model.auth.login.response.Registers
import com.retail.dolphinpos.domain.model.auth.logout.LogoutResponse
import com.retail.dolphinpos.domain.model.auth.select_registers.reponse.UpdateStoreRegisterData
import com.retail.dolphinpos.domain.model.auth.select_registers.reponse.UpdateStoreRegisterResponse
import com.retail.dolphinpos.domain.model.auth.select_registers.request.UpdateStoreRegisterRequest
import com.retail.dolphinpos.domain.model.home.catrgories_products.CategoryData
import com.retail.dolphinpos.domain.model.home.catrgories_products.ProductImage
import com.retail.dolphinpos.domain.model.home.catrgories_products.Products
import com.retail.dolphinpos.domain.model.home.catrgories_products.ProductsResponse
import com.retail.dolphinpos.domain.model.home.catrgories_products.Variant
import com.retail.dolphinpos.domain.model.home.catrgories_products.VariantImage
import com.retail.dolphinpos.domain.model.home.catrgories_products.Vendor
import com.retail.dolphinpos.domain.repositories.auth.StoreRegistersRepository

class StoreRegisterRepositoryImpl(
    private val api: ApiService,
    private val userDao: UserDao,
    private val productsDao: ProductsDao,
) : StoreRegistersRepository {

    override suspend fun updateStoreRegister(updateStoreRegisterRequest: UpdateStoreRegisterRequest): UpdateStoreRegisterResponse {
        return try {
            api.updateStoreRegister(updateStoreRegisterRequest)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getProducts(storeID: Int, locationID: Int): ProductsResponse {
        return try {
            api.getProducts(storeID, locationID)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun logout(): LogoutResponse {
        return try {
            api.logout()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getLocations(storeID: Int): List<Locations> {
        val locationEntities = userDao.getLocationsByStoreId(storeID)
        return UserMapper.toLocationsAgainstStoreID(locationEntities)
    }

    override suspend fun getRegistersByLocationID(locationID: Int): List<Registers> {
        val registerEntities = userDao.getRegistersByLocationId(locationID)
        return UserMapper.toRegistersAgainstLocationID(locationID, registerEntities)
    }

    override suspend fun insertRegisterStatusDetailsIntoLocalDB(updateStoreRegisterData: UpdateStoreRegisterData) {
        try {
            userDao.insertRegisterStatusDetails(
                UserMapper.toRegisterStatusEntity(
                    updateStoreRegisterData
                )
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getRegisterStatus(): UpdateStoreRegisterData {
        val registerStatusDetailEntities = userDao.getRegisterStatusDetail()
        return UserMapper.toRegisterStatus(registerStatusDetailEntities)
    }

    override suspend fun insertCategoriesIntoLocalDB(categoryList: List<CategoryData>) {
        try {
            val categoryEntities = categoryList.map { category ->
                ProductMapper.toCategoryEntity(
                    category = category
                )
            }
            productsDao.insertCategories(categoryEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertProductsIntoLocalDB(productList: List<Products>, categoryId: Int) {
        try {
            val productEntities = productList.map { product ->
                ProductMapper.toProductEntity(
                    products = product,
                    categoryId = categoryId
                )
            }
            productsDao.insertProducts(productEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertProductImagesIntoLocalDB(
        productImageList: List<ProductImage>,
        productId: Int
    ) {
        try {
            val productImagesEntities = productImageList.map { productImage ->
                ProductMapper.toProductImagesEntity(
                    productImages = productImage,
                    productId = productId
                )
            }
            productsDao.insertProductImages(productImagesEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertProductVariantsIntoLocalDB(
        productVariantList: List<Variant>,
        productId: Int
    ) {
        try {
            val productVariantEntities = productVariantList.map { productVariant ->
                ProductMapper.toProductVariantsEntity(
                    variants = productVariant,
                    productId = productId
                )
            }
            productsDao.insertVariants(productVariantEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertVariantImagesIntoLocalDB(
        variantImageList: List<VariantImage>,
        variantId: Int
    ) {
        try {
            val variantImagesEntities = variantImageList.map { variantImage ->
                ProductMapper.toVariantImagesEntity(
                    variantImages = variantImage,
                    variantId = variantId
                )
            }
            productsDao.insertVariantImages(variantImagesEntities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertVendorDetailsIntoLocalDB(vendor: Vendor, productId: Int) {
        try {
            productsDao.insertVendor(
                ProductMapper.toProductVendorEntity(
                    vendor = vendor, productId = productId
                )
            )
        } catch (e: Exception) {
            throw e
        }
    }
}