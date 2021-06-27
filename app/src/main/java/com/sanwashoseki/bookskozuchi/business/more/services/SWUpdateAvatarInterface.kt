package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 14/12/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUploadAvatarResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWUpdateAvatarInterface: IRefreshable {

    fun updateAvatarSuccess(result: SWUploadAvatarResponse)

    fun showMsgUpdateAvatarError(msg: String)
}