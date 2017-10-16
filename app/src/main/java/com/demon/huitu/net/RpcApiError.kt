/*
 * 文件名: RpcApiError
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.net

/**
 * 服务器返回的错误信息<BR></BR>

 * @author zhaozeyang
 * *
 * @version [Taobei Client V20160411, 16/4/18]
 */
class RpcApiError(val httpCode: Int, val message: String)
