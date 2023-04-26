import Flutter
import UIKit
import libPhoneNumber_iOS

public class SwiftLibPhoneNumberPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "codeheadlabs.com/libphonenumber", binaryMessenger: registrar.messenger())
    let instance = SwiftLibPhoneNumberPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }



  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
  }
}
