package com.clouddroid.pettypetscarehealth.model

/**
 * Created by arkadiusz on 16.02.18.
 */
data class MedicalRecord(val key: String = "",
                         val title: String = "",
                         val date: String = "",
                         val hospital: String = "",
                         val comment: String = "")