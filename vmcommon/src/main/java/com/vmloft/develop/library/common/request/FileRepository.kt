package com.vmloft.develop.library.common.request

import cn.ucloud.ufile.UfileClient
import cn.ucloud.ufile.api.`object`.ObjectConfig
import cn.ucloud.ufile.api.`object`.policy.PolicyParam
import cn.ucloud.ufile.api.`object`.policy.PutPolicyForCallback
import cn.ucloud.ufile.auth.ObjectRemoteAuthorization
import cn.ucloud.ufile.auth.UfileObjectRemoteAuthorization
import cn.ucloud.ufile.bean.PutObjectResultBean
import cn.ucloud.ufile.exception.UfileClientException
import cn.ucloud.ufile.exception.UfileServerException
import cn.ucloud.ufile.util.JLog

import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File

/**
 * Create by lzan13 on 2021/7/5
 * 描述：文件 管理相关处理
 */
class FileRepository : BaseRepository() {

    /**
     * 上传图片，这里一般是处理过的图片，比如压缩，裁剪，不需要进行二次处理
     * @param file 需要上传的文件
     * @param subPath 上传子目录 TODO 目前 sdk 好像没有办法指定子目录，后续实现
     */
    suspend fun uploadPicture(file: File, subPath: String = "images${File.separator}avatar"): RResult<String> = withContext(Dispatchers.IO) {
        JLog.SHOW_TEST = true
        JLog.SHOW_DEBUG = true
        try {
            // 通过本地签名授权器
//            val auth = UfileObjectLocalAuthorization(CConstants.ucloudPublicKey(), CConstants.ucloudPrivateKey())
            // 通过远程接口签名授权
            val authUrl = CConstants.baseHost() + "v1/third/ucloud/signatureObj"
            val apiConfig = ObjectRemoteAuthorization.ApiConfig(authUrl, authUrl)
            val auth = UfileObjectRemoteAuthorization(CConstants.ucloudPublicKey(), apiConfig)

            // 对象操作配置，需要配置存储对象所属区域或者绑定的域名 https://docs.ucloud.cn/api/summary/regionlist
//            val config = ObjectConfig("cn-bj", "ufileos.com")
            val config = ObjectConfig(CConstants.mediaHost())
            // 生成文件名
            val filename = "${VMStr.toMD5(file.path)}${VMFile.parseSuffix(file.path)}"
            val mimeType = VMFile.getMimeType(filename)
            // 回调策略
//            val paramList = mutableListOf<PolicyParam>()
//            paramList.add(PolicyParam("filename", filename))
//            paramList.add(PolicyParam("extname", VMFile.parseSuffix(file.path)))
//            paramList.add(PolicyParam("path", "${CConstants.baseHost()}${filename}"))
//            val callbackUrl = CConstants.baseHost() + "v1/third/ucloud/callbackObj"
//            val putPolicy = PutPolicyForCallback.Builder(callbackUrl, paramList).build()
            val response: PutObjectResultBean = UfileClient.`object`(auth, config)
                .putObject(file, mimeType)
                .nameAs(filename)
                .toBucket(CConstants.ucloudBucket())
//                .toBucket("${CConstants.ucloudBucket()}${File.separator}${subPath}")
//                // 是否上传校验MD5, Default = true
//                .withVerifyMd5(false)
//                // 指定progress callback的间隔, Default = 每秒回调
//                .withProgressConfig(ProgressConfig.callbackWithPercent(10))
//                // 配置进度监听
//                .setOnProgressListener { bytesWritten, contentLength ->
//                    VMLog.d("上传进度 $bytesWritten, $contentLength")
//                }
//                .withPutPolicy(putPolicy)
                .execute()

            // 生成访问 url
            val url = UfileClient.`object`(auth, config)
                .getDownloadUrlFromPublicBucket(filename, CConstants.ucloudBucket())
                .createUrl()

            VMLog.d("上传结果地址 $url")
            // 获取图片大小
            val sizes = VMBitmap.getImageSize(file.path)
            var w = sizes[0]
            var h = sizes[1]
            // 防止宽高比差距过大，这里做个重新赋值限制下
            if (w > h * 2) {
                w = h * 2
            } else if (h > w * 2) {
                h = w * 2
            }
            return@withContext RResult.Success("", "$url?w=$w&h=$h")
        } catch (e: UfileClientException) {
            VMLog.d("上传图片失败 ${e.message}")
            return@withContext RResult.Error(-1, "上传文件失败 ${e.message}")
        } catch (e: UfileServerException) {
            VMLog.d("上传图片失败 ${e.errorBean.retCode} ${e.errorBean.errMsg}")
            return@withContext RResult.Error(e.errorBean.retCode, "上传文件失败 ${e.errorBean.errMsg}")
        } catch (e: Exception) {
            VMLog.d("上传图片失败 ${e.message}")
            return@withContext RResult.Error(-1, "上传文件失败 ${e.message}")
        }
    }

}