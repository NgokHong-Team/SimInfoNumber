# INFO SIM + LIB PHONE NUMBER 

///Coding example 


     PhoneNumberUtil.getInfoSim().then((value) async {
        print("haha => ${value?.simIso} --- ${value?.simCountryIso}");
        final phoneFormat = await PhoneNumberUtil.formatAsYouType(
            phoneNumber: "076552814",
            isoCode: value?.simIso ?? value?.simCountryIso ?? "vn");

        print("hahaha 3 => $phoneFormat");
      });
