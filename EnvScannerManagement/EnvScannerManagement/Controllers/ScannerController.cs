using Database;
using EnvScannerManagement.Models;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Web;
using System.Web.Http;
using System.Web.Mvc;



namespace EnvScannerManagement.Controllers
{
    public class ScannerController : ApiController
    {
        public class JSONSerializableAndroidData
        {
            //Device
            public string Id { get; set; }

            //General
            public string AndroidAPI { get; set; }
            public int NumberOfWifiConnections { get; set; }
            public int NumberOfBtConnections { get; set; }
            public double GPSlatitude { get; set; }
            public double GPSLongtitude { get; set; }
            public string DateAndTime { get; set; }


            //Wifi
            public List<Wifi> Wifis { get; set; }

            //Bluetooth
            public List<Bluetooth> Bluetooths { get; set; }

        }

        public void Get()
        {
            //TestMethod
        }

        public string Post(JSONSerializableAndroidData model)
        {
            AddDataToDatabase(model);
            return null;
        }

        private void AddDataToDatabase(JSONSerializableAndroidData model)
        {
            try {
                using (var db = new DatabaseContext())
                {
                    var newGeneral = new General
                    {
                        AndroidAPI = model.AndroidAPI,
                        Bluetooths = model.Bluetooths,
                        DateAndTime = DateTime.Parse(model.DateAndTime),
                        DeviceId = model.Id,
                        GPSlatitude = model.GPSlatitude,
                        GPSLongtitude = model.GPSLongtitude,
                        NumberOfBtConnections = model.NumberOfBtConnections,
                        NumberOfWifiConnections = model.NumberOfWifiConnections,
                        Wifis = model.Wifis
                    };

                    bool deviceExist = db.Devices.Any(x => x.Id.Equals(model.Id));
                    if (deviceExist)
                        db.Generals.Add(newGeneral);
                    else
                    {
                        db.Devices.Add(new Device()
                        {
                            Id = model.Id,
                            General = new List<General>() { newGeneral }
                        });
                    }

                    db.SaveChanges();
                }
            }
            catch (Exception ex)
            {
                Debugger.Log(100, "Adding data to database", ex.Message);
            }
        }
    }
}