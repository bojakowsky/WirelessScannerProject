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

namespace EnvScannerManagement.Controllers
{
    public class GeneralsController : Controller
    {
        private DatabaseContext db = new DatabaseContext();

        // GET: Generals
        public async Task<ActionResult> Index()
        {
            var generals = db.Generals.Include(g => g.Device);
            return View(await generals.ToListAsync());
        }

        // GET: Generals/Details/5
        public async Task<ActionResult> Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            General general = await db.Generals.FindAsync(id);
            if (general == null)
            {
                return HttpNotFound();
            }
            return View(general);
        }

        // GET: Generals/Create
        public ActionResult Create()
        {
            ViewBag.DeviceId = new SelectList(db.Devices, "Id", "Id");
            return View();
        }

        // POST: Generals/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Create([Bind(Include = "Id,AndroidAPI,NumberOfWifiConnections,NumberOfBtConnections,GPSlatitude,GPSLongtitude,DateAndTime,DeviceId")] General general)
        {
            if (ModelState.IsValid)
            {
                db.Generals.Add(general);
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }

            ViewBag.DeviceId = new SelectList(db.Devices, "Id", "Id", general.DeviceId);
            return View(general);
        }

        // GET: Generals/Edit/5
        public async Task<ActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            General general = await db.Generals.FindAsync(id);
            if (general == null)
            {
                return HttpNotFound();
            }
            ViewBag.DeviceId = new SelectList(db.Devices, "Id", "Id", general.DeviceId);
            return View(general);
        }

        // POST: Generals/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Edit([Bind(Include = "Id,AndroidAPI,NumberOfWifiConnections,NumberOfBtConnections,GPSlatitude,GPSLongtitude,DateAndTime,DeviceId")] General general)
        {
            if (ModelState.IsValid)
            {
                db.Entry(general).State = EntityState.Modified;
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }
            ViewBag.DeviceId = new SelectList(db.Devices, "Id", "Id", general.DeviceId);
            return View(general);
        }

        // GET: Generals/Delete/5
        public async Task<ActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            General general = await db.Generals.FindAsync(id);
            if (general == null)
            {
                return HttpNotFound();
            }
            return View(general);
        }

        // POST: Generals/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmed(int id)
        {
            General general = await db.Generals.FindAsync(id);
            db.Generals.Remove(general);
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
