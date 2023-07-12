import Flutter
import UIKit
import libPhoneNumber_iOS
import CoreTelephony

public class SwiftLibPhoneNumberPlugin: NSObject, FlutterPlugin {

   var phoneUtil: NBPhoneNumberUtil!


  public static func register(with registrar: FlutterPluginRegistrar) {
   let channel = FlutterMethodChannel(name: "hahalolo.com/libphonenumber", binaryMessenger: registrar.messenger())
   let instance = SwiftLibPhoneNumberPlugin()
   instance.phoneUtil = NBPhoneNumberUtil()
   registrar.addMethodCallDelegate(instance, channel: channel)
  }



  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
  var err: NSError?

     let arg = call.arguments as? Dictionary<String,Any>

          let phoneNumber = arg?["phone_number"] as? String
           let isoCode = arg?["iso_code"] as? String
           let formatEnumString = arg?["format"] as? String
  var number: NBPhoneNumber?

  // Call formatAsYouType before parse below because a partial number will not be parsable.
  if call.method == "formatAsYouType" {
  if let f = NBAsYouTypeFormatter(regionCode: isoCode) {
  result(f.inputString(phoneNumber))
  return
  }
  }

  if phoneNumber != nil {
  number = try? self.phoneUtil.parse(phoneNumber, defaultRegion: isoCode)
  if number == nil {
  result(FlutterError(code: "invalid_phone_number", message: "Invalid Phone Number", details: nil))
  return
  }
  }

  if call.method == "isValidPhoneNumber" {
  let validNumber = self.phoneUtil.isValidNumber(number)
  result(validNumber)
  } else if call.method == "normalizePhoneNumber" {
  let normalizedNumber = try? self.phoneUtil.format(number, numberFormat: .E164)
  if normalizedNumber == nil {
  result(FlutterError(code: "invalid_national_number", message: "Invalid phone number for the country specified", details: nil))
  return
  }
  result(normalizedNumber)
  } else if call.method == "getInfoSim" {
  let networkInfo = CTTelephonyNetworkInfo()
  let carrier = networkInfo.subscriberCellularProvider

  if carrier?.mobileCountryCode == nil || carrier?.isoCountryCode == nil {
  result(FlutterError(code: "", message: "can't detect on sim swift", details: nil))
  return
  }

  result([
  "simNetworkIso": carrier?.mobileCountryCode ?? "",
  "simCountryIso": carrier?.isoCountryCode ?? "",
  ])
  } else if call.method == "getRegionInfo" {
  let regionCode = self.phoneUtil.getRegionCode(for: number)
  let countryCode = self.phoneUtil.getCountryCode(forRegion: regionCode)
  let formattedNumber = try? self.phoneUtil.format(number, numberFormat: .NATIONAL)
  if formattedNumber == nil {
  result(FlutterError(code: "invalid_national_number", message: "Invalid phone number for the country specified", details: nil))
  return
  }

  result([
  "isoCode": regionCode ?? "",
  "regionCode": countryCode?.stringValue ?? "",
  "formattedPhoneNumber": formattedNumber ?? "",
  ])
  } else if call.method == "getExampleNumber" {
  let exampleNumber = try? self.phoneUtil.getExampleNumber(isoCode)
  let regionCode = self.phoneUtil.getRegionCode(for: exampleNumber)
  let formattedNumber = try? self.phoneUtil.format(exampleNumber, numberFormat: .NATIONAL)
  if formattedNumber == nil {
  result(FlutterError(code: "invalid_national_number", message: "Invalid phone number for the country specified", details: nil))
  return
  }

  result([
  "isoCode": regionCode ?? "",
  "formattedPhoneNumber": formattedNumber ?? "",
  ])
  } else if call.method == "getNumberType" {
  let numberType = self.phoneUtil.getNumberType(number).rawValue
  result(numberType)
  } else if call.method == "getNameForNumber" {
  let name = ""
  result(name)
  } else if call.method == "format" {
  var formattedNumber: String?
  switch formatEnumString {
  case "NATIONAL":
  formattedNumber = try? self.phoneUtil.format(number, numberFormat: .NATIONAL)
  case "INTERNATIONAL":
  formattedNumber = try? self.phoneUtil.format(number, numberFormat: .INTERNATIONAL)
  case "E164":
  formattedNumber = try? self.phoneUtil.format(number, numberFormat: .E164)
  case "RFC3966":
  formattedNumber = try? self.phoneUtil.format(number, numberFormat: .RFC3966)
  default:
  break
  }

  if formattedNumber == nil {
  result(FlutterError(code: "Error \(err?.code ?? 0)", message: err?.domain, details: err?.localizedDescription))
  return
  }
  result(formattedNumber)
  } else {
  result(FlutterMethodNotImplemented)
  }


  }
}
