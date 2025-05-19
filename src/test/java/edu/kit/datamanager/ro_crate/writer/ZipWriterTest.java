package edu.kit.datamanager.ro_crate.writer;

import java.io.IOException;
import java.nio.file.Path;

import edu.kit.datamanager.ro_crate.Crate;
import edu.kit.datamanager.ro_crate.RoCrate;

class ZipWriterTest implements
        CommonWriterTest,
        ElnFileWriterTest
{
    @Override
    public void saveCrate(Crate crate, Path target) throws IOException {
        Writers.newZipPathWriter()
                .save(crate, target.toAbsolutePath().toString());
    }

    @Override
    public void saveCrateElnStyle(Crate crate, Path target) throws IOException {
        new CrateWriter<>(new WriteZipStrategy().usingElnStyle())
                .save(crate, target.toAbsolutePath().toString());
    }

    @Override
    public void saveCrateSubdirectoryStyle(RoCrate crate, Path target) throws IOException {
        new CrateWriter<>(new WriteZipStrategy().withRootSubdirectory())
                .save(crate, target.toString());
    }
}
