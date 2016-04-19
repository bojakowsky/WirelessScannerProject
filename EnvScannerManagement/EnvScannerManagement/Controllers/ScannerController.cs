using Database;
using EnvScannerManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace EnvScannerManagement.Controllers
{
    public class ScannerController : Controller
    {
        // GET: Scanner
        public ActionResult Index()
        {
            return View();
        }

        // POST
        public ActionResult CollectAndroidData(AndroidDataViewModel model)
        {
            using (var db = new DatabaseContext())
            {
                db.Devices.Add(model);
                db.SaveChanges();
            }

            return null;
        }
    }
}