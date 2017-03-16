package org.dotnet.ci.util;

// Contains functionality to deal with agents.
class Agents {
    // Retrieves the machine affinity for a build that needs docker
    // Parameters:
    //  version - Version to use.  Typically either latest or an alias corresponding to the target product
    // Returns:
    //  Label for the VM to use
    static String getDockerAgentLabel(String version) {
        switch (version) {
            // Latest version
            case 'latest':
                return getAgentLabel('Ubuntu16.04', 'latest-docker')
                break
            
            // Current version in use for netcore 2.0
            case 'netcore2.0':
                return getAgentLabel('Ubuntu16.04', '20170216')
                break

            default:
                assert false : "Version ${version} not recognized"
        }
    }

    // Given the name of an OS and image version, get the label that
    // this task would run on
    //
    // Parameters:
    //  job: Job to set affinity for
    //  osName: Name of OS to to run on.
    //  version: Version of the image
    // Returns:
    //  String representing the label that the task should target
    static String getAgentLabel(String osName, String version) {
        if (osName == 'Ubuntu') {
            osName = 'Ubuntu14.04'
        }
        // Special case OSX.  We did not use to have
        // an OS version.  Current OSX job run against 10.11
        if (osName == 'OSX') {
            osName = 'OSX10.11'
        }

        // Move off of "latest" by simply removing the 'or-auto' bit
        version = version.replace('-or-auto', '')

        def machineMap    = [
                            'Ubuntu14.04' :
                                [
                                // Specific auto-image label
                                '201626':'auto-ubuntu1404-201626',
                                // Contains an updated version of mono
                                '20160211':'auto-ubuntu1404-20160211.1',
                                // Contains npm, njs, nvm
                                '20161020':'ubuntu1404-20161020',
                                // Contains 20160211-1 + gdb + mono 4.6.2.16
                                '20170109':'ubuntu1404-20170109',
                                // Contains 20160211-1 + clang 3.9
                                '20170118':'ubuntu1404-20170118',
                                // Contains the rootfs setup for arm/arm64 builds.  Move this label forward
                                // till we have the working build/test, then apply to everything.
                                'arm-cross-latest':'auto-ubuntu1404-20170120',
                                // Latest auto image.
                                'latest':'auto-ubuntu1404-20160211.1',
                                // For outerloop runs.
                                'outer-latest':'auto-ubuntu1404-201626outer',
                                // For internal Ubuntu runs
                                'latest-internal':'auto-ubuntu1404-20160211.1-internal'
                                ],
                            'Ubuntu15.10' :
                                [
                                // Latest auto image.
                                'latest':'auto-ubuntu1510-20160307',
                                // For outerloop runs.
                                'outer-latest':'auto-ubuntu1510-20160307outer'
                                ],
                            'Ubuntu16.04' :
                                [
                                // Explicit versions:

                                // Contains auto-ubuntu1604-20160803 + gdb + mono 4.6.2.16
                                '20170109':'ubuntu1604-20170109',
                                '20170216':'ubuntu1604-20170216',

                                // Aliases:

                                // Latest auto image.
                                'latest':'ubuntu1604-20170216',
                                // auto-ubuntu1604-20160510 + docker.
                                // Move this to latest after validation
                                'latest-docker':'ubuntu1604-20170216',
                                // For outerloop runs.
                                'outer-latest':'ubuntu1604-20170216-outer',
                                // For outerloop runs, using Linux kernel version 4.6.2
                                'outer-linux462': 'auto-auto-ubuntu1604-20160510-20160715outer'
                                ],
                            'Ubuntu16.10' :
                                [
                                // Latest auto image.  This will be used for transitioning
                                // to the auto images, at which point we will move back to
                                // the generic unversioned label except for special cases.
                                'latest':'ubuntu1610-20170216',
                                // For outerloop runs.
                                'outer-latest':'ubuntu1610-20170216-outer',
                                ],
                            'OSX10.11' :
                                [
                                // Latest auto image.
                                'latest':'mac',
                                // For elevated runs
                                'latest-elevated':'mac-elevated'
                                ],
                            // El Capitan
                            'OSX10.11' :
                                [
                                // Latest auto image.
                                'latest':'osx-10.11',
                                // For elevated runs
                                'latest-elevated':'osx-10.11-elevated'
                                ],
                            // Sierra
                            'OSX10.12' :
                                [
                                // Latest auto image.
                                'latest':'osx-10.12',
                                // For elevated runs
                                'latest-elevated':'osx-10.12-elevated'
                                ],
                            // This is Windows Server 2012 R2
                            'Windows_NT' :
                                [
                                // Older images.  VS update 1
                                '20160325' : 'auto-win2012-20160325',
                                // Older images.  VS update 1
                                '20160325-elevated' : 'auto-win2012-20160325-elevated',
                                // Older images.  VS update 3
                                '20160627' : 'auto-win2012-20160627',
                                // Older images.  VS update 3
                                '20160627-elevated' : 'auto-win2012-20160627-elevated',
                                // auto-win2012-20160824 + .NET 4.6.2
                                '20161027' : 'win2012-20161027',
                                // Latest auto image.
                                // the generic unversioned label except for special cases.
                                'latest':'win2012-20170303',
                                // Win2012.R2 + VS2013.5 + VS2015.3 + VS15.P3
                                'latest-dev15':'auto-win2012-20160707',
                                // Win2012.R2 + VS2013.5 + VS2015.3 + VS15.P4
                                'latest-dev15-preview4':'auto-win2012-20160912',
                                // Win2016 + VS15.P5
                                'latest-dev15-preview5':'win2016-20161013-1',
                                // Win2016 + VS15.RC2
                                'latest-dev15-rc2':'win2016-20170105',
                                // Win2016 + VS15.RC4
                                'latest-dev15-rc':'win2016-20170214',
                                // Win2016 + VS15.0
                                'latest-dev15-0':'win2016-20170307',
                                // Dev15 image
                                'latest-dev15':'auto-win2012-20160506',
                                // For internal runs
                                'latest-internal':'auto-win2012-20160707-internal',
                                // For internal runs - Win2016 + VS15.RC2
                                'latest-dev15-rc2-internal':'win2016-20170105-internal',
                                // For internal runs - Win2016 + VS15.RC4
                                'latest-dev15-internal':'win2016-20170214-internal',
                                // For internal runs - Win2016 + VS15.0
                                'latest-dev15-0-internal':'win2016-20170307-internal',
                                // For internal runs which don't need/want the static 'windows-internal' pool
                                'latest-dev15-internal':'auto-win2012-20160707-internal',
                                // For elevated runs
                                'latest-elevated':'win2012-20170303-elevated',
                                // For perf runs
                                'latest-perf':'windows-perf-internal',
                                // Win2016
                                'win2016-base': 'win2016-base',
                                // Win2016
                                'win2016-base-internal': 'win2016-base-internal'
                                ],
                            'Windows_2016' :
                                [
                                // First working containers image
                                'win2016-20161018-1':'win2016-20161018-1',
                                // Latest auto image w/docker (move to latest when possible)
                                'latest-containers':'win2016-20161018-1',
                                // Latest auto image.
                                'latest':'auto-win2016-20160223'
                                ],
                            'Windows Nano 2016' :
                                [
                                // Generic version label
                                '' : 'windowsnano16'
                                ],
                            'Windows 10' :
                                [
                                // Latest auto image.
                                'latest':'win2016-20170303'
                                ],
                            'Windows 7' :
                                [
                                '20161104':'win2008-20170303',
                                // Latest auto image.
                                'latest':'win2008-20170303'
                                ],
                            'FreeBSD' :
                                [
                                // Latest auto image.
                                'latest':'freebsd-20161026'
                                ],
                            'RHEL7.2' :
                                [
                                // Latest auto image.
                                'latest':'auto-rhel72-20160211',
                                // For outerloop runs.
                                'outer-latest':'auto-rhel72-20160412.1outer'
                                ],
                            'CentOS7.1' :
                                [
                                // Latest auto image.
                                'latest':'centos71-20170216',
                                // For outerloop runs.
                                'outer-latest':'centos71-20170216-outer',
                                // For outerloop runs, using Linux kernel version 4.6.4
                                'outer-linux464': 'auto-auto-centos71-20160609.1-20160715outer'
                                ],
                            'OpenSUSE13.2' :
                                [
                                // Latest auto image.
                                'latest':'auto-suse132-20160315',
                                // For outerloop runs
                                'outer-latest':'auto-suse132-20160315outer'
                                ],
                            'OpenSUSE42.1' :
                                [
                                // Latest auto image.
                                'latest':'suse421-20170216',
                                // For outerloop runs
                                'outer-latest':'suse421-20170216-outer'
                                ],
                            'Debian8.2' :
                                [
                                '20160323':'auto-deb82-20160323',
                                // Latest auto image.
                                'latest':'auto-deb82-20160323'
                                ],
                            'Debian8.4' :
                                [
                                // Latest auto image.
                                'latest':'deb84-20170214',
                                // For outerloop runs
                                'outer-latest':'deb84-20170214-outer'
                                ],
                           'Fedora23' :
                                [
                                // Latest auto image.
                                'latest':'auto-fedora23-20160622',
                                // For outerloop runs
                                'outer-latest':'auto-fedora23-20160622outer'
                                ],
                            'Fedora24' :
                                [
                                // Latest auto image.
                                'latest':'fedora24-20161024',
                                // For outerloop runs
                                'outer-latest':'fedora24-20161024-outer'
                                ],
                            'Tizen' :
                                [
                                // Use ubuntu14.04 images
                                // Contains the rootfs setup for arm/arm64 builds.  Move this label forward
                                // till we have the working build/test, then apply to everything.
                                'arm-cross-latest':'auto-ubuntu1404-20170120',
                                // Latest auto image.
                                'latest':'auto-ubuntu1404-20170120',
                                ],
                                // Some nodes don't have git, which is what is required for the
                                // generators.
                            'Generators' :
                                [
                                'latest':'!windowsnano16 && !arm64 && !performance'
                                ]
                            ]
        def versionLabelMap = machineMap.get(osName, null)
        assert versionLabelMap != null : "Could not find os ${osName}"
        def machineLabel = versionLabelMap.get(version, null)
        assert machineLabel != null : "Could not find version ${version} of ${osName}"
        
        return machineLabel
    }
}