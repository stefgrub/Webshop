package com.example.shop.web.admin;

import com.example.shop.service.ImageStorageService;
import com.example.shop.service.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/media")
public class AdminMediaController {

    private final ImageStorageService storage;

    @GetMapping
    public String view(Model model) {
        List<MediaFile> files = storage.listAll();
        model.addAttribute("files", files);
        return "admin/media";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               RedirectAttributes ra) {
        try {
            String url = storage.store(file);
            ra.addFlashAttribute("success", "Bild wurde erfolgreich hochgeladen.");
            ra.addFlashAttribute("lastUrl", url);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Fehler beim Upload: " + e.getMessage());
        }
        return "redirect:/admin/media";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("filename") String filename,
                         RedirectAttributes ra) {
        try {
            storage.deleteByFilename(filename);
            ra.addFlashAttribute("success", "Datei wurde gelöscht: " + filename);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Fehler beim Löschen: " + e.getMessage());
        }
        return "redirect:/admin/media";
    }

    @PostMapping("/delete-multiple")
    public String deleteMultiple(@RequestParam(name = "selected", required = false) List<String> selected,
                                 RedirectAttributes ra) {
        if (selected == null || selected.isEmpty()) {
            ra.addFlashAttribute("error", "Keine Dateien ausgewählt.");
            return "redirect:/admin/media";
        }
        try {
            storage.deleteMultiple(selected);
            ra.addFlashAttribute("success", selected.size() + " Datei(en) wurden gelöscht.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Fehler beim Löschen: " + e.getMessage());
        }
        return "redirect:/admin/media";
    }

    @PostMapping("/rename")
    public String rename(@RequestParam("oldFilename") String oldFilename,
                         @RequestParam("newFilename") String newFilename,
                         RedirectAttributes ra) {
        try {
            storage.rename(oldFilename, newFilename);
            ra.addFlashAttribute("success", "Datei wurde umbenannt: " + oldFilename + " → " + newFilename);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Fehler beim Umbenennen: " + e.getMessage());
        }
        return "redirect:/admin/media";
    }
}