using Database;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;

namespace EnvScannerManagement.Controllers
{

    [Authorize(Roles = "OkUser, Admin")]
    public class MapController : Controller
    {

        private DatabaseContext db = new DatabaseContext();

        // GET: Generals
        public async Task<ActionResult> Index()
        {
            db.Configuration.ProxyCreationEnabled = false;
            var generals = db.Generals;
            return View(await generals.AsNoTracking().ToListAsync());
        }

        public async Task<ActionResult> Bluetooths()
        {
            db.Configuration.ProxyCreationEnabled = false;
            var bluetooths = db.Bluetooths.Include(w => w.General).AsQueryable();
            return View(await bluetooths.AsNoTracking().ToListAsync());
        }

        public async Task<ActionResult> Wifis()
        {
            db.Configuration.ProxyCreationEnabled = false;
            //var openWifis = db.Wifis.Include(w => w.General).Where(x => x.Security.Equals("[ESS]")).AsQueryable();
            var wifis = db.Wifis.Include(w => w.General).AsQueryable();
            //return View(await openWifis.AsNoTracking().OrderByDescending(x => x.General.DateAndTime).ToListAsync());
            return View(await wifis.AsNoTracking().ToListAsync());
        }

        struct WifiAndBluetoothMerged
        {
            public List<DTOBluetooth> bt;
            public List<DTOWifi> wifi;
            public List<DTOGeneral> general;
        }

        public async Task<ActionResult> GetDetails(int generalId)
        {
            var wifiDetails = await db.Wifis.AsNoTracking().Where(y => y.GeneralId == generalId).Select(x => new DTOWifi()
            {
                BSSID = x.BSSID,
                Frequency = x.Frequency,
                Level = x.Level,
                Security = x.Security,
                SSID = x.SSID,
                Timestamp = x.Timestamp
            }).ToListAsync();

            var bluetoothDetails = await db.Bluetooths.AsNoTracking().Where(y => y.GeneralId == generalId).Select(x => new DTOBluetooth()
            {
                DeviceName = x.DeviceName,
                MAC = x.MAC
            }).ToListAsync();

            var generalDetails = await db.Generals.AsNoTracking().Where(y => y.Id == generalId).Select(x => new DTOGeneral()
            {
                AndroidAPI = x.AndroidAPI,
                DateAndTime = x.DateAndTime,
                DeviceId = x.DeviceId,
                GPSlatitude = x.GPSlatitude,
                GPSLongtitude = x.GPSLongtitude,
                NumberOfBtConnections = x.NumberOfBtConnections,
                NumberOfWifiConnections = x.NumberOfWifiConnections
            }).ToListAsync();

            var merged = new WifiAndBluetoothMerged()
            {
                bt = bluetoothDetails,
                wifi = wifiDetails,
                general = generalDetails
            };
            
            return Json(merged, JsonRequestBehavior.AllowGet);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}