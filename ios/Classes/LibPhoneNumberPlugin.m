#import "LibPhoneNumberPlugin.h"
#import "NBPhoneNumberUtil.h"
#import "NBAsYouTypeFormatter.h"
#import<CoreTelephony/CTCallCenter.h>
#import<CoreTelephony/CTCall.h>
#import<CoreTelephony/CTCarrier.h>
#import<CoreTelephony/CTTelephonyNetworkInfo.h>
#if __has_include(<lib_phone_number_plugin/lib_phone_number_plugin-Swift.h>)
#import <lib_phone_number_plugin/lib_phone_number_plugin-Swift.h>
#else
#import "lib_phone_number_plugin-Swift.h"

#endif


@interface LibPhoneNumberPlugin ()
@property(nonatomic, retain) NBPhoneNumberUtil *phoneUtil;
@end

@implementation LibPhoneNumberPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
   [SwiftLibPhoneNumberPlugin registerWithRegistrar:registrar];

//   FlutterMethodChannel* channel = [FlutterMethodChannel methodChannelWithName:@"hahalolo.com/libphonenumber"
//                                                                 binaryMessenger:[registrar messenger]];
//
//     LibPhoneNumberPlugin* instance = [[LibPhoneNumberPlugin alloc] init];
//     instance.phoneUtil = [[NBPhoneNumberUtil alloc] init];
//
//     [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
 }

@end
