package com.dokugo.data.response

data class GetIncomeResponse(
	val incomeResponse: List<IncomeResponseItem>
)

data class IncomeResponseItem(
	val transactionId: Int,
	val id: String,
	val amount: Int,
	val category: Int,
	val date: String,
	val notes: String,
	val createdAt: String,
	val updatedAt: String
)
