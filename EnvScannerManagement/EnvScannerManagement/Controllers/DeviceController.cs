using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Database;
using System.Diagnostics;

namespace EnvScannerManagement.Controllers
{
    [Authorize(Roles = "OkUser, Admin")]
    public class DeviceController : Controller
    {
        private DatabaseContext db = new DatabaseContext();

        // GET: Device
        public async Task<ActionResult> Index()
        {

            DTODevice device = new DTODevice();
            device.Id = await db.Devices.Select(x=>x.Id).ToListAsync();
            var distinctFrequencies = db.Wifis.Select(x => x.Frequency).Distinct();
            try {
                foreach (var freq in distinctFrequencies)
                {
                    var count = await db.Wifis.Distinct().CountAsync(x => x.Frequency == freq);
                    device.frequencies.Add(new Frequency()
                    {
                        frequency = freq,
                        numberOfUses = count
                    });
                }
            }
            catch(Exception e)
            {
                Debug.WriteLine(e.Message);
            }

            device.sumOfBluetoothCapture = await db.Bluetooths.Select(x=>x.MAC).Distinct().CountAsync();
            device.sumOfWifisCapture = await db.Wifis.Select(x=>x.BSSID).Distinct().CountAsync();
            device.totalCaptures = device.sumOfWifisCapture + device.sumOfBluetoothCapture;

            device.uniqueBSSIDCount = await db.Wifis.Select(x => x.BSSID).Distinct().CountAsync();
            device.uniqueMACsCount = await db.Bluetooths.Select(x => x.MAC).Distinct().CountAsync();

            return View(device);
        }


        // GET: Device/Delete/5
        [Authorize(Roles = "Admin")]
        public async Task<ActionResult> Delete(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Device device = await db.Devices.FindAsync(id);
            if (device == null)
            {
                return HttpNotFound();
            }
            return View(device);
        }

        // POST: Device/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        [Authorize(Roles ="Admin")]
        public async Task<ActionResult> DeleteConfirmed(string id)
        {
            Device device = await db.Devices.FindAsync(id);
            db.Devices.Remove(device);
            await db.SaveChangesAsync();
            return RedirectToAction("Index");
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
