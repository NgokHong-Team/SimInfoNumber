package com.example.lib_phone_number_plugin

import android.app.Activity
import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.i18n.phonenumbers.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.android.FlutterActivity
import java.lang.ref.WeakReference
import io.flutter.plugin.common.BinaryMessenger

import java.util.*


/** LibPhoneNumberPlugin */
class LibPhoneNumberPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
    private val phoneNumberToCarrierMapper: PhoneNumberToCarrierMapper =
        PhoneNumberToCarrierMapper.getInstance()

    private var mActivity: WeakReference<Activity>? = null
    private val getActivity get() = mActivity?.get()
    private lateinit var methodChannel: MethodChannel
    private lateinit var binaryMessenger: BinaryMessenger


    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        binaryMessenger = binding.binaryMessenger
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = WeakReference(binding.activity)
        methodChannel = MethodChannel(binaryMessenger, "hahalolo.com/libphonenumber")
        methodChannel.setMethodCallHandler(this@LibPhoneNumberPlugin)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        mActivity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        mActivity = WeakReference(binding.activity)
    }

    override fun onDetachedFromActivity() {
        mActivity = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel?.setMethodCallHandler(null)
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "isValidPhoneNumber" -> handleIsValidPhoneNumber(call, result)
            "normalizePhoneNumber" -> handleNormalizePhoneNumber(call, result)
            "getRegionInfo" -> handleGetRegionInfo(call, result)
            "getNumberType" -> handleGetNumberType(call, result)
            "getExampleNumber" -> handleGetExampleNumber(call, result)
            "formatAsYouType" -> formatAsYouType(call, result)
            "getNameForNumber" -> handleGetNameForNumber(call, result)
            "getInfoSim" -> getInfoSim(result)
            "format" -> handleFormat(call, result)
            else -> result.notImplemented()
        }
    }

    private fun getInfoSim(result: Result) {
        val manager: TelephonyManager =
            getActivity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountryIso: String = manager.simCountryIso
        val simNetworkIso: String = manager.networkCountryIso
        result.success(mapOf("simCountryIso" to simCountryIso, "simNetworkIso" to simNetworkIso))
    }

    private fun handleGetNameForNumber(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        try {
            val p: Phonenumber.PhoneNumber =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            result.success(phoneNumberToCarrierMapper.getNameForNumber(p, Locale.getDefault()))
        } catch (e: NumberParseException) {
            result.error("NumberParseException", e.message, null)
        }
    }

    private fun handleFormat(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        val format = call.argument<String>("format")
        try {
            val p: Phonenumber.PhoneNumber =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            val phoneNumberFormat: PhoneNumberUtil.PhoneNumberFormat =
                PhoneNumberUtil.PhoneNumberFormat.valueOf(format ?: "")
            result.success(phoneUtil.format(p, phoneNumberFormat))
        } catch (e: Exception) {
            result.error("Exception", e.message, null)
        }
    }

    private fun handleIsValidPhoneNumber(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        try {
            val p: Phonenumber.PhoneNumber =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            result.success(phoneUtil.isValidNumber(p))
        } catch (e: NumberParseException) {
            result.error("NumberParseException", e.message, null)
        }
    }

    private fun handleNormalizePhoneNumber(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        try {
            val p: Phonenumber.PhoneNumber =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            val normalized: String = phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.E164)
            result.success(normalized)
        } catch (e: NumberParseException) {
            result.error("NumberParseException", e.message, null)
        }
    }

    private fun handleGetRegionInfo(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        try {
            val p: Phonenumber.PhoneNumber? =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            val regionCode: String = phoneUtil.getRegionCodeForNumber(p)
            val countryCode: String = java.lang.String.valueOf(p?.countryCode)
            val formattedNumber: String =
                phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            result.success(
                mutableMapOf(
                    "isoCode" to regionCode,
                    "regionCode" to countryCode,
                    "formattedPhoneNumber" to formattedNumber
                )
            )
        } catch (e: NumberParseException) {
            result.error("NumberParseException", e.message, null)
        }
    }

    private fun handleGetExampleNumber(call: MethodCall, result: Result) {
        val isoCode = call.argument<String>("iso_code")
        val p: Phonenumber.PhoneNumber = phoneUtil.getExampleNumber(isoCode)
        val regionCode: String = phoneUtil.getRegionCodeForNumber(p)
        val formattedNumber: String =
            phoneUtil.format(p, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        val resultMap: Map<String, String> = HashMap()
        result.success(
            mutableMapOf(
                "isoCode" to regionCode,
                "formattedPhoneNumber" to formattedNumber
            )
        )
    }

    private fun handleGetNumberType(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        try {
            val p: Phonenumber.PhoneNumber =
                phoneUtil.parse(phoneNumber, isoCode!!.uppercase(Locale.getDefault()))
            when (phoneUtil.getNumberType(p)) {
                PhoneNumberUtil.PhoneNumberType.FIXED_LINE -> result.success(0)
                PhoneNumberUtil.PhoneNumberType.MOBILE -> result.success(1)
                PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE -> result.success(2)
                PhoneNumberUtil.PhoneNumberType.TOLL_FREE -> result.success(3)
                PhoneNumberUtil.PhoneNumberType.PREMIUM_RATE -> result.success(4)
                PhoneNumberUtil.PhoneNumberType.SHARED_COST -> result.success(5)
                PhoneNumberUtil.PhoneNumberType.VOIP -> result.success(6)
                PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER -> result.success(7)
                PhoneNumberUtil.PhoneNumberType.PAGER -> result.success(8)
                PhoneNumberUtil.PhoneNumberType.UAN -> result.success(9)
                PhoneNumberUtil.PhoneNumberType.VOICEMAIL -> result.success(10)
                PhoneNumberUtil.PhoneNumberType.UNKNOWN -> result.success(-1)
                else -> result.success(-1)
            }
        } catch (e: NumberParseException) {
            result.error("NumberParseException", e.message, null)
        }
    }

    private fun formatAsYouType(call: MethodCall, result: Result) {
        val phoneNumber = call.argument<String>("phone_number")
        val isoCode = call.argument<String>("iso_code")
        val asYouTypeFormatter: AsYouTypeFormatter = phoneUtil.getAsYouTypeFormatter(
            isoCode!!.uppercase(
                Locale.getDefault()
            )
        )
        var res: String? = null
        for (i in 0 until phoneNumber!!.length) {
            res = asYouTypeFormatter.inputDigit(phoneNumber[i])
        }
        result.success(res)
    }
}
