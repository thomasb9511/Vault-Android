package com.lobstr.stellar.tsmapper.presentation.entities.transaction

import android.os.Parcelable
import com.lobstr.stellar.tsmapper.presentation.entities.transaction.operation.Operation
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val fee: Long,
    val envelopXdr: String,
    val sourceAccount: String,
    val memo: TsMemo,
    val operations: List<Operation>,
    var sequenceNumber: Long = 0,
    var transactionType: String? = null
) : Parcelable